use serde::{Deserialize, Serialize};
use sqlx::FromRow;
use uuid::Uuid;
use chrono::{DateTime, Utc};
use utoipa::ToSchema;

#[derive(Debug, Serialize, Deserialize, sqlx::Type, ToSchema)]
#[sqlx(type_name = "friend_status", rename_all = "lowercase")]
pub enum FriendStatus {
    Pending,
    Accepted,
    Rejected,
    Cancelled,
    Blocked,
}

#[derive(Debug, FromRow, Serialize, ToSchema)]
#[schema(example = json!({
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "sender_id": "123e4567-e89b-12d3-a456-426614174001",
    "receiver_id": "123e4567-e89b-12d3-a456-426614174002",
    "status": "pending",
    "created_at": "2024-03-21T12:00:00Z",
    "updated_at": "2024-03-21T12:00:00Z"
}))]
pub struct FriendRequest {
    pub id: Uuid,
    pub sender_id: Uuid,
    pub receiver_id: Uuid,
    pub status: FriendStatus,
    pub created_at: DateTime<Utc>,
    pub updated_at: DateTime<Utc>,
    pub expires_at: DateTime<Utc>,
}

#[derive(Debug, FromRow, Serialize, ToSchema)]
#[schema(example = json!({
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "user_id": "123e4567-e89b-12d3-a456-426614174001",
    "friend_id": "123e4567-e89b-12d3-a456-426614174002",
    "created_at": "2024-03-21T12:00:00Z"
}))]
pub struct Friend {
    pub user_id: Uuid,
    pub friend_id: Uuid,
    pub created_at: DateTime<Utc>,
}

#[derive(Debug, Deserialize, ToSchema)]
#[schema(example = json!({
    "receiver_id": "123e4567-e89b-12d3-a456-426614174000"
}))]
pub struct FriendRequestPayload {
    pub receiver_id: Uuid,
}

#[derive(Debug, Deserialize, ToSchema)]
#[schema(example = json!({
    "request_id": "123e4567-e89b-12d3-a456-426614174000"
}))]
pub struct AcceptRequestPayload {
    pub request_id: Uuid,
}

#[derive(Debug, Deserialize, ToSchema)]
#[schema(example = json!({
    "request_id": "123e4567-e89b-12d3-a456-426614174000"
}))]
pub struct RejectRequestPayload {
    pub request_id: Uuid,
}

#[derive(Debug, Deserialize, ToSchema)]
#[schema(example = json!({
    "request_id": "123e4567-e89b-12d3-a456-426614174000"
}))]
pub struct CancelRequestPayload {
    pub request_id: Uuid,
}

#[derive(Debug, Deserialize, ToSchema)]
pub struct UserSearchQuery {
    #[schema(example = "john")]
    pub query: String,
    #[schema(example = 1)]
    #[serde(default = "default_page")]
    pub page: i32,
    #[schema(example = 20)]
    #[serde(default = "default_limit")]
    pub limit: i32,
}

#[derive(Debug, Deserialize, ToSchema)]
pub struct PaginationQuery {
    #[schema(example = 1)]
    #[serde(default = "default_page")]
    pub page: i32,
    #[schema(example = 20)]
    #[serde(default = "default_limit")]
    pub limit: i32,
}

#[derive(Debug, Serialize, ToSchema)]
pub struct UserSearchResult {
    #[serde(flatten)]
    pub user: UserBasicInfo,
    #[serde(rename = "status")]
    pub friend_status: UserFriendStatus,
}

#[derive(Debug, Serialize, ToSchema)]
#[serde(rename_all = "camelCase")]
pub struct UserBasicInfo {
    #[serde(rename = "userId")]
    pub id: Uuid,
    pub username: String,
    pub email: String,
    pub full_name: String,
}

#[derive(Debug, Serialize, ToSchema)]
#[serde(rename_all = "snake_case")]
pub enum UserFriendStatus {
    None,
    Accepted,
    PendingOutgoing,
    PendingIncoming,
}

#[derive(Debug, Serialize, ToSchema)]
pub struct PaginatedResponse<T> {
    pub items: Vec<T>,
    pub page: i32,
    pub limit: i32,
    pub has_more: bool,
}

#[derive(Debug, Serialize, ToSchema)]
pub struct PaginationInfo {
    pub page: i32,
    pub limit: i32,
    pub has_more: bool,
}

fn default_page() -> i32 {
    1
}

fn default_limit() -> i32 {
    20
} 