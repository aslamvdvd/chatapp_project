use serde::{Deserialize, Serialize};
use sqlx::FromRow;
use uuid::Uuid;
use chrono::{DateTime, Utc};

#[derive(Debug, Serialize, Deserialize, sqlx::Type)]
#[sqlx(type_name = "friend_status", rename_all = "lowercase")]
pub enum FriendStatus {
    Pending,
    Accepted,
    Declined,
    Blocked,
}

#[derive(Debug, FromRow, Serialize)]
pub struct FriendRequest {
    pub id: Uuid,
    pub sender_id: Uuid,
    pub receiver_id: Uuid,
    pub status: FriendStatus,
    pub created_at: DateTime<Utc>,
    pub updated_at: DateTime<Utc>,
}

#[derive(Debug, FromRow, Serialize)]
pub struct Friend {
    pub user_id: Uuid,
    pub friend_id: Uuid,
    pub created_at: DateTime<Utc>,
}

#[derive(Debug, Deserialize)]
pub struct FriendRequestPayload {
    pub receiver_id: Uuid,
}

#[derive(Debug, Deserialize)]
pub struct AcceptRequestPayload {
    pub request_id: Uuid,
}

#[derive(Debug, Deserialize)]
pub struct UserSearchQuery {
    pub query: String,
} 