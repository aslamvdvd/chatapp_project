use actix_web::{web::{Data, Json, Query}, HttpResponse};
use crate::{
    models::friends::{
        AcceptRequestPayload, CancelRequestPayload, FriendRequestPayload, PaginationQuery,
        RejectRequestPayload, UserSearchQuery,
    },
    services::friend_service::{FriendService, FriendServiceError},
    utils::jwt::AuthenticatedUser,
};
use tracing::info;

#[derive(Debug, thiserror::Error)]
pub enum FriendHandlerError {
    #[error("Service error: {0}")]
    Service(#[from] FriendServiceError),
    #[error("Invalid request")]
    InvalidRequest,
}

impl actix_web::ResponseError for FriendHandlerError {
    fn error_response(&self) -> HttpResponse {
        match self {
            Self::Service(FriendServiceError::Database(_)) => {
                HttpResponse::InternalServerError().json(serde_json::json!({
                    "error": "Internal server error"
                }))
            }
            Self::Service(FriendServiceError::RequestNotFound) => {
                HttpResponse::NotFound().json(serde_json::json!({
                    "error": "Friend request not found"
                }))
            }
            Self::Service(FriendServiceError::InvalidRequestState) => {
                HttpResponse::BadRequest().json(serde_json::json!({
                    "error": "Invalid request state"
                }))
            }
            Self::Service(FriendServiceError::Unauthorized) => {
                HttpResponse::Forbidden().json(serde_json::json!({
                    "error": "Unauthorized action"
                }))
            }
            Self::Service(FriendServiceError::SelfRequest) => {
                HttpResponse::BadRequest().json(serde_json::json!({
                    "error": "Cannot send friend request to yourself"
                }))
            }
            Self::Service(FriendServiceError::DuplicateRequest) => {
                HttpResponse::Conflict().json(serde_json::json!({
                    "error": "Friend request already exists"
                }))
            }
            Self::InvalidRequest => {
                HttpResponse::BadRequest().json(serde_json::json!({
                    "error": "Invalid request parameters"
                }))
            }
        }
    }
}

/// Send a friend request to another user
#[utoipa::path(
    post,
    path = "/friends/request",
    request_body = FriendRequestPayload,
    responses(
        (status = 201, description = "Friend request sent successfully"),
        (status = 400, description = "Invalid request"),
        (status = 403, description = "Unauthorized action"),
        (status = 409, description = "Request already exists")
    ),
    security(
        ("bearer_auth" = [])
    )
)]
pub async fn send_request(
    user: AuthenticatedUser,
    friend_service: Data<FriendService>,
    payload: Json<FriendRequestPayload>,
) -> Result<HttpResponse, FriendHandlerError> {
    info!(sender_id = %user.user_id, receiver_id = %payload.receiver_id, "Sending friend request");
    
    let request = friend_service
        .send_friend_request(user.user_id, payload.receiver_id)
        .await?;

    Ok(HttpResponse::Created().json(request))
}

/// Accept a friend request
#[utoipa::path(
    post,
    path = "/friends/accept",
    request_body = AcceptRequestPayload,
    responses(
        (status = 204, description = "Friend request accepted"),
        (status = 400, description = "Invalid request"),
        (status = 403, description = "Unauthorized action"),
        (status = 404, description = "Request not found")
    ),
    security(
        ("bearer_auth" = [])
    )
)]
pub async fn accept_request(
    user: AuthenticatedUser,
    friend_service: Data<FriendService>,
    payload: Json<AcceptRequestPayload>,
) -> Result<HttpResponse, FriendHandlerError> {
    info!(user_id = %user.user_id, request_id = %payload.request_id, "Accepting friend request");
    
    friend_service
        .accept_friend_request(payload.request_id, user.user_id)
        .await?;

    Ok(HttpResponse::NoContent().finish())
}

/// Reject a friend request
#[utoipa::path(
    post,
    path = "/friends/reject",
    request_body = RejectRequestPayload,
    responses(
        (status = 204, description = "Friend request rejected"),
        (status = 400, description = "Invalid request"),
        (status = 403, description = "Unauthorized action"),
        (status = 404, description = "Request not found")
    ),
    security(
        ("bearer_auth" = [])
    )
)]
pub async fn reject_request(
    user: AuthenticatedUser,
    friend_service: Data<FriendService>,
    payload: Json<RejectRequestPayload>,
) -> Result<HttpResponse, FriendHandlerError> {
    info!(user_id = %user.user_id, request_id = %payload.request_id, "Rejecting friend request");
    
    friend_service
        .reject_friend_request(payload.request_id, user.user_id)
        .await?;

    Ok(HttpResponse::NoContent().finish())
}

/// Cancel a sent friend request
#[utoipa::path(
    post,
    path = "/friends/cancel",
    request_body = CancelRequestPayload,
    responses(
        (status = 204, description = "Friend request cancelled"),
        (status = 400, description = "Invalid request"),
        (status = 403, description = "Unauthorized action"),
        (status = 404, description = "Request not found")
    ),
    security(
        ("bearer_auth" = [])
    )
)]
pub async fn cancel_request(
    user: AuthenticatedUser,
    friend_service: Data<FriendService>,
    payload: Json<CancelRequestPayload>,
) -> Result<HttpResponse, FriendHandlerError> {
    info!(user_id = %user.user_id, request_id = %payload.request_id, "Cancelling friend request");
    
    friend_service
        .cancel_friend_request(payload.request_id, user.user_id)
        .await?;

    Ok(HttpResponse::NoContent().finish())
}

/// Get list of friends
#[utoipa::path(
    get,
    path = "/friends/list",
    params(
        ("page" = i32, Query, description = "Page number (1-based)"),
        ("limit" = i32, Query, description = "Number of items per page")
    ),
    responses(
        (status = 200, description = "List of friends retrieved successfully"),
        (status = 400, description = "Invalid parameters")
    ),
    security(
        ("bearer_auth" = [])
    )
)]
pub async fn list_friends(
    user: AuthenticatedUser,
    friend_service: Data<FriendService>,
    query: Query<PaginationQuery>,
) -> Result<HttpResponse, FriendHandlerError> {
    info!(user_id = %user.user_id, "Fetching friends list");
    
    let friends = friend_service
        .get_friends_list(user.user_id, query.page, query.limit)
        .await?;

    Ok(HttpResponse::Ok().json(friends))
}

/// Get list of incoming friend requests
#[utoipa::path(
    get,
    path = "/friends/requests",
    params(
        ("page" = i32, Query, description = "Page number (1-based)"),
        ("limit" = i32, Query, description = "Number of items per page")
    ),
    responses(
        (status = 200, description = "List of friend requests retrieved successfully"),
        (status = 400, description = "Invalid parameters")
    ),
    security(
        ("bearer_auth" = [])
    )
)]
pub async fn list_requests(
    user: AuthenticatedUser,
    friend_service: Data<FriendService>,
    query: Query<PaginationQuery>,
) -> Result<HttpResponse, FriendHandlerError> {
    info!(user_id = %user.user_id, "Fetching friend requests");

    let requests = friend_service
        .get_friend_requests(user.user_id, query.page, query.limit)
        .await?;

    Ok(HttpResponse::Ok().json(requests))
}

/// Search for users
#[utoipa::path(
    get,
    path = "/users/search",
    params(
        ("query" = String, Query, description = "Search query"),
        ("page" = i32, Query, description = "Page number (1-based)"),
        ("limit" = i32, Query, description = "Number of items per page")
    ),
    responses(
        (status = 200, description = "Search results retrieved successfully"),
        (status = 400, description = "Invalid parameters")
    ),
    security(
        ("bearer_auth" = [])
    )
)]
pub async fn search_users(
    user: AuthenticatedUser,
    friend_service: Data<FriendService>,
    query: Query<UserSearchQuery>,
) -> Result<HttpResponse, FriendHandlerError> {
    info!(user_id = %user.user_id, query = %query.query, "Searching users");
    
    let results = friend_service
        .search_users(user.user_id, &query.query, query.page, query.limit)
        .await?;

    Ok(HttpResponse::Ok().json(results))
} 