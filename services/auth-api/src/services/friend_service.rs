use sqlx::{PgPool, Result};
use uuid::Uuid;
use crate::models::friends::FriendRequest;
use crate::models::user::UserProfile;

pub async fn send_friend_request(pool: &PgPool, sender_id: Uuid, receiver_id: Uuid) -> Result<FriendRequest> {
    let request = sqlx::query_as!(
        FriendRequest,
        r#"
        INSERT INTO friend_requests (sender_id, receiver_id, status)
        VALUES ($1, $2, 'pending')
        RETURNING id, sender_id, receiver_id, status AS "status: _", created_at, updated_at
        "#,
        sender_id,
        receiver_id
    )
    .fetch_one(pool)
    .await?;
    Ok(request)
}

pub async fn accept_friend_request(pool: &PgPool, request_id: Uuid, current_user_id: Uuid) -> Result<()> {
    let mut tx = pool.begin().await?;

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
    .fetch_one(&mut *tx)
    .await?;

    sqlx::query!(
        "INSERT INTO friends (user_id, friend_id) VALUES ($1, $2), ($2, $1)",
        request.sender_id,
        request.receiver_id
    )
    .execute(&mut *tx)
    .await?;

    tx.commit().await?;
    Ok(())
}

pub async fn get_friends_list(pool: &PgPool, user_id: Uuid) -> Result<Vec<UserProfile>> {
    let friends = sqlx::query_as!(
        UserProfile,
        r#"
        SELECT u.id, u.username, u.email, u.created_at, u.updated_at
        FROM users u
        INNER JOIN friends f ON u.id = f.friend_id
        WHERE f.user_id = $1
        "#,
        user_id
    )
    .fetch_all(pool)
    .await?;
    Ok(friends)
}

pub async fn search_users(pool: &PgPool, query: &str) -> Result<Vec<UserProfile>> {
    let users = sqlx::query_as!(
        UserProfile,
        r#"
        SELECT id, username, email, created_at, updated_at
        FROM users
        WHERE username ILIKE $1 OR email ILIKE $1
        LIMIT 20
        "#,
        format!("%{}%", query)
    )
    .fetch_all(pool)
    .await?;
    Ok(users)
} 