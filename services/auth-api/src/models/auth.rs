// backend/src/models/auth.rs
// This file will contain structs related to authentication, such as login requests and responses. 

use crate::models::user::UserInfoResponse;
use serde::{Deserialize, Serialize};
use validator::Validate;
use utoipa::ToSchema;

/// Represents the request payload for user login.
#[derive(Debug, Deserialize, Serialize, Validate, ToSchema)]
#[schema(example = json!({
    "email": "user@example.com",
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
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "token_type": "Bearer",
    "user": {
        "id": "uuid-of-user",
        "username": "exampleuser",
        "email": "example@example.com",
        "profilePic": null
    }
}))]
pub struct LoginResponse {
    /// The JWT access token.
    pub access_token: String,
    /// The type of token.
    pub token_type: String,
    pub user: UserInfoResponse,
} 