use crate::models::friends::{FriendRequest, UserSearchResult, PaginatedResponse, UserBasicInfo, UserFriendStatus};
use sqlx::{PgPool, Result};
use uuid::Uuid;
use tracing::{info, warn, error, instrument};
use chrono::{Utc, Duration};

const DEFAULT_PAGE_SIZE: i32 = 20;
const MAX_PAGE_SIZE: i32 = 100;

#[derive(Debug, thiserror::Error)]
pub enum FriendServiceError {
    #[error("Database error: {0}")]
    Database(#[from] sqlx::Error),
    #[error("Request not found")]
    RequestNotFound,
    #[error("Invalid request state")]
    InvalidRequestState,
    #[error("Unauthorized action")]
    Unauthorized,
    #[error("Cannot send request to self")]
    SelfRequest,
    #[error("Request already exists")]
    DuplicateRequest,
}

#[derive(Clone)]
pub struct FriendService {
    pool: PgPool,
}

impl FriendService {
    pub fn new(pool: PgPool) -> Self {
        Self { pool }
    }

    #[instrument(skip(self), fields(sender_id = %sender_id, receiver_id = %receiver_id))]
    pub async fn send_friend_request(&self, sender_id: Uuid, receiver_id: Uuid) -> Result<FriendRequest, FriendServiceError> {
        if sender_id == receiver_id {
            return Err(FriendServiceError::SelfRequest);
        }

        let mut tx = self.pool.begin().await?;

        // Check for existing pending request
        let existing_request = sqlx::query!(
            r#"
            SELECT id FROM friend_requests 
            WHERE (sender_id = $1 AND receiver_id = $2) 
            OR (sender_id = $2 AND receiver_id = $1)
            AND status = 'pending'
            "#,
            sender_id,
            receiver_id
        )
        .fetch_optional(&mut *tx)
        .await?;

        if existing_request.is_some() {
            return Err(FriendServiceError::DuplicateRequest);
        }

        let expires_at = Utc::now() + Duration::days(7);

        // Create new friend request
        let request = sqlx::query_as!(
            FriendRequest,
            r#"
            INSERT INTO friend_requests (sender_id, receiver_id, status, expires_at)
            VALUES ($1, $2, 'pending', $3)
            RETURNING id, sender_id, receiver_id, status as "status: _", created_at, updated_at, expires_at
            "#,
            sender_id,
            receiver_id,
            expires_at
        )
        .fetch_one(&mut *tx)
        .await?;

        tx.commit().await?;
        info!(request_id = %request.id, "Friend request sent successfully");
        Ok(request)
    }

    #[instrument(skip(self), fields(request_id = %request_id, current_user_id = %current_user_id))]
    pub async fn accept_friend_request(&self, request_id: Uuid, current_user_id: Uuid) -> Result<(), FriendServiceError> {
        let mut tx = self.pool.begin().await?;

        let request = sqlx::query!(
            r#"
            UPDATE friend_requests
            SET status = 'accepted', updated_at = NOW()
            WHERE id = $1 AND receiver_id = $2 AND status = 'pending'
            RETURNING sender_id, receiver_id
            "#,
            request_id,
            current_user_id
        )
        .fetch_optional(&mut *tx)
        .await?;

        let request = match request {
            Some(r) => r,
            None => {
                tx.rollback().await?;
                return Err(FriendServiceError::RequestNotFound);
            }
        };

        sqlx::query!(
            "INSERT INTO friends (user_id, friend_id) VALUES ($1, $2), ($2, $1)",
            request.sender_id,
            request.receiver_id
        )
        .execute(&mut *tx)
        .await?;

        tx.commit().await?;
        info!("Friend request accepted successfully");
        Ok(())
    }

    #[instrument(skip(self), fields(request_id = %request_id, current_user_id = %current_user_id))]
    pub async fn reject_friend_request(&self, request_id: Uuid, current_user_id: Uuid) -> Result<(), FriendServiceError> {
        let result = sqlx::query!(
            r#"
            UPDATE friend_requests
            SET status = 'rejected', updated_at = NOW()
            WHERE id = $1 AND receiver_id = $2 AND status = 'pending'
            "#,
            request_id,
            current_user_id
        )
        .execute(&self.pool)
        .await?;

        if result.rows_affected() == 0 {
            warn!("Attempt to reject non-existent or non-pending request");
            return Err(FriendServiceError::RequestNotFound);
        }

        info!("Friend request rejected successfully");
        Ok(())
    }

    #[instrument(skip(self), fields(request_id = %request_id, current_user_id = %current_user_id))]
    pub async fn cancel_friend_request(&self, request_id: Uuid, current_user_id: Uuid) -> Result<(), FriendServiceError> {
        let result = sqlx::query!(
            r#"
            UPDATE friend_requests
            SET status = 'cancelled', updated_at = NOW()
            WHERE id = $1 AND sender_id = $2 AND status = 'pending'
            "#,
            request_id,
            current_user_id
        )
        .execute(&self.pool)
        .await?;

        if result.rows_affected() == 0 {
            warn!("Attempt to cancel non-existent or non-pending request");
            return Err(FriendServiceError::RequestNotFound);
        }

        info!("Friend request cancelled successfully");
        Ok(())
    }

    #[instrument(skip(self), fields(user_id = %user_id))]
    pub async fn get_friends_list(&self, user_id: Uuid, page: i32, limit: i32) -> Result<PaginatedResponse<UserBasicInfo>, FriendServiceError> {
        let offset = (page - 1) * limit;
        
        let friends = sqlx::query_as!(
            UserBasicInfo,
            r#"
            SELECT u.id, u.username, u.email, 
                   CONCAT(u.first_name, ' ', u.last_name) as "full_name!"
            FROM users u
            INNER JOIN friends f ON u.id = f.friend_id
            WHERE f.user_id = $1
            ORDER BY u.username
            LIMIT $2 OFFSET $3
            "#,
            user_id,
            limit as i64,
            offset as i64
        )
        .fetch_all(&self.pool)
        .await?;

        let total_count = sqlx::query!(
            "SELECT COUNT(*) as count FROM friends WHERE user_id = $1",
            user_id
        )
        .fetch_one(&self.pool)
        .await?;

        let has_more = ((offset + limit) as i64) < total_count.count.unwrap_or(0);

        Ok(PaginatedResponse {
            items: friends,
            page,
            limit,
            has_more,
        })
    }

    #[instrument(skip(self), fields(user_id = %user_id))]
    pub async fn get_friend_requests(&self, user_id: Uuid, page: i32, limit: i32) -> Result<PaginatedResponse<FriendRequest>, FriendServiceError> {
        let offset = (page - 1) * limit;

        let requests = sqlx::query_as!(
            FriendRequest,
            r#"
            SELECT id, sender_id, receiver_id, status as "status: _", created_at, updated_at, expires_at
            FROM friend_requests
            WHERE receiver_id = $1 AND status = 'pending'
            ORDER BY created_at DESC
            LIMIT $2 OFFSET $3
            "#,
            user_id,
            limit as i64,
            offset as i64
        )
        .fetch_all(&self.pool)
        .await?;

        let total_count = sqlx::query!(
            "SELECT COUNT(*) as count FROM friend_requests WHERE receiver_id = $1 AND status = 'pending'",
            user_id
        )
        .fetch_one(&self.pool)
        .await?;

        let has_more = ((offset + limit) as i64) < total_count.count.unwrap_or(0);

        Ok(PaginatedResponse {
            items: requests,
            page,
            limit,
            has_more,
        })
    }

    #[instrument(skip(self), fields(current_user_id = %current_user_id, query = %query))]
    pub async fn search_users(&self, current_user_id: Uuid, query: &str, page: i32, limit: i32) -> Result<PaginatedResponse<UserSearchResult>, FriendServiceError> {
        let offset = (page - 1) * limit;
        
        let users = sqlx::query!(
            r#"
            WITH user_relations AS (
                SELECT 
                    u.id,
                    u.username,
                    u.email,
                    u.first_name,
                    u.last_name,
                    u.is_private_profile,
                    CASE
                        WHEN f.friend_id IS NOT NULL THEN 'accepted'::text
                        WHEN fr_sent.id IS NOT NULL THEN 'pending_outgoing'::text
                        WHEN fr_received.id IS NOT NULL THEN 'pending_incoming'::text
                        ELSE 'none'::text
                    END as friend_status
                FROM users u
                LEFT JOIN friends f ON f.friend_id = u.id AND f.user_id = $1
                LEFT JOIN friend_requests fr_sent ON fr_sent.receiver_id = u.id 
                    AND fr_sent.sender_id = $1 AND fr_sent.status = 'pending'
                LEFT JOIN friend_requests fr_received ON fr_received.sender_id = u.id 
                    AND fr_received.receiver_id = $1 AND fr_received.status = 'pending'
                WHERE 
                    u.id != $1
                    AND (
                        u.username ILIKE $2 
                        OR u.email ILIKE $2 
                        OR u.first_name ILIKE $2 
                        OR u.last_name ILIKE $2
                    )
            )
            SELECT *
            FROM user_relations
            WHERE 
                NOT is_private_profile 
                OR friend_status IN ('accepted', 'pending_incoming')
            ORDER BY username
            LIMIT $3 OFFSET $4
            "#,
            current_user_id,
            format!("%{}%", query),
            limit as i64,
            offset as i64
        )
        .fetch_all(&self.pool)
        .await?;

        let search_results = users.into_iter().map(|u| {
            UserSearchResult {
                user: UserBasicInfo {
                    id: u.id,
                    username: u.username,
                    email: u.email,
                    full_name: format!("{} {}", u.first_name, u.last_name),
                },
                friend_status: match u.friend_status.unwrap_or_else(|| "none".to_string()).as_str() {
                    "accepted" => UserFriendStatus::Accepted,
                    "pending_outgoing" => UserFriendStatus::PendingOutgoing,
                    "pending_incoming" => UserFriendStatus::PendingIncoming,
                    _ => UserFriendStatus::None,
                },
            }
        }).collect();

        let total_count = sqlx::query!(
            r#"
            SELECT COUNT(*) as count
            FROM users u
            LEFT JOIN friends f ON f.friend_id = u.id AND f.user_id = $1
            WHERE 
                u.id != $1
                AND (
                    u.username ILIKE $2 
                    OR u.email ILIKE $2 
                    OR u.first_name ILIKE $2 
                    OR u.last_name ILIKE $2
                )
                AND (
                    NOT u.is_private_profile 
                    OR f.friend_id IS NOT NULL
                )
            "#,
            current_user_id,
            format!("%{}%", query)
        )
        .fetch_one(&self.pool)
        .await?;

        let has_more = ((offset + limit) as i64) < total_count.count.unwrap_or(0);

        Ok(PaginatedResponse {
            items: search_results,
            page,
            limit,
            has_more,
        })
    }
} 