// backend/src/models/auth.rs
// This file will contain structs related to authentication, such as login requests and responses. 

use serde::{Deserialize, Serialize};
use validator::Validate;
use utoipa::ToSchema;
use crate::models::user::UserPublicData;

/// Represents the request payload for user login.
#[derive(Debug, Deserialize, Serialize, Validate, ToSchema)]
#[schema(example = json!({
    "email_or_username": "user@example.com",
    "password": "Password123!"
}))]
pub struct LoginRequest {
    /// The user's email address or username.
    #[validate(length(min = 1, message = "Email or username cannot be empty"))]
    pub email_or_username: String,

    /// The user's password.
    #[validate(length(min = 8, message = "Password must be at least 8 characters long"))]
    pub password: String,
}

/// Represents the response payload for a successful user login.
#[derive(Debug, Serialize, Deserialize, ToSchema)]
#[schema(example = json!({
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
        "id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
        "username": "example_user",
        "email": "user@example.com",
        "created_at": "2025-01-01T12:00:00Z",
        "updated_at": "2025-01-01T12:00:00Z"
    }
}))]
pub struct LoginResponse {
    /// The JWT access token.
    pub token: String,
    /// The public data of the authenticated user.
    pub user: UserPublicData,
} 