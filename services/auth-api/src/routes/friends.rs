use actix_web::{web, HttpResponse, Responder, get, post};
use crate::models::friends::{FriendRequestPayload, AcceptRequestPayload, RejectRequestPayload, CancelRequestPayload, UserSearchQuery};
use crate::services::friend_service;
use crate::db::PgPool;
use crate::core::rbac::AuthenticatedUser;
use utoipa;

#[utoipa::path(
    post,
    path = "/friends/request",
    request_body = FriendRequestPayload,
    security(("bearer_auth" = [])),
    responses(
        (status = 201, description = "Friend request sent successfully", body = FriendRequest),
        (status = 400, description = "Invalid request or self-request"),
        (status = 401, description = "Unauthorized"),
        (status = 403, description = "Rate limit exceeded"),
        (status = 404, description = "User not found")
    )
)]
#[post("/request")]
async fn send_friend_request(
    pool: web::Data<PgPool>,
    user: AuthenticatedUser,
    payload: web::Json<FriendRequestPayload>,
) -> impl Responder {
    if user.id == payload.receiver_id {
        return HttpResponse::BadRequest().json("Cannot send a friend request to yourself.");
    }
    
    match friend_service::send_friend_request(&pool, user.id, payload.receiver_id).await {
        Ok(request) => HttpResponse::Created().json(request),
        Err(_) => HttpResponse::InternalServerError().finish(),
    }
}

#[utoipa::path(
    post,
    path = "/friends/accept",
    request_body = AcceptRequestPayload,
    security(("bearer_auth" = [])),
    responses(
        (status = 200, description = "Friend request accepted successfully"),
        (status = 400, description = "Invalid request"),
        (status = 401, description = "Unauthorized"),
        (status = 404, description = "Request not found")
    )
)]
#[post("/accept")]
async fn accept_friend_request(
    pool: web::Data<PgPool>,
    user: AuthenticatedUser,
    payload: web::Json<AcceptRequestPayload>,
) -> impl Responder {
    match friend_service::accept_friend_request(&pool, payload.request_id, user.id).await {
        Ok(_) => HttpResponse::Ok().finish(),
        Err(sqlx::Error::RowNotFound) => HttpResponse::NotFound().json("Friend request not found or not in pending state"),
        Err(_) => HttpResponse::InternalServerError().finish(),
    }
}

#[utoipa::path(
    post,
    path = "/friends/reject",
    request_body = RejectRequestPayload,
    security(("bearer_auth" = [])),
    responses(
        (status = 204, description = "Friend request rejected successfully"),
        (status = 400, description = "Invalid request"),
        (status = 401, description = "Unauthorized"),
        (status = 403, description = "Not the receiver of the request"),
        (status = 404, description = "Request not found")
    )
)]
#[post("/reject")]
async fn reject_friend_request(
    pool: web::Data<PgPool>,
    user: AuthenticatedUser,
    payload: web::Json<RejectRequestPayload>,
) -> impl Responder {
    match friend_service::reject_friend_request(&pool, payload.request_id, user.id).await {
        Ok(_) => HttpResponse::NoContent().finish(),
        Err(sqlx::Error::RowNotFound) => HttpResponse::NotFound().json("Friend request not found or not in pending state"),
        Err(_) => HttpResponse::InternalServerError().finish(),
    }
}

#[utoipa::path(
    post,
    path = "/friends/cancel",
    request_body = CancelRequestPayload,
    security(("bearer_auth" = [])),
    responses(
        (status = 204, description = "Friend request cancelled successfully"),
        (status = 400, description = "Invalid request"),
        (status = 401, description = "Unauthorized"),
        (status = 403, description = "Not the sender of the request"),
        (status = 404, description = "Request not found")
    )
)]
#[post("/cancel")]
async fn cancel_friend_request(
    pool: web::Data<PgPool>,
    user: AuthenticatedUser,
    payload: web::Json<CancelRequestPayload>,
) -> impl Responder {
    match friend_service::cancel_friend_request(&pool, payload.request_id, user.id).await {
        Ok(_) => HttpResponse::NoContent().finish(),
        Err(sqlx::Error::RowNotFound) => HttpResponse::NotFound().json("Friend request not found or not in pending state"),
        Err(_) => HttpResponse::InternalServerError().finish(),
    }
}

#[utoipa::path(
    get,
    path = "/friends/list",
    params(
        ("page" = Option<i32>, Query, description = "Page number (1-based)"),
        ("limit" = Option<i32>, Query, description = "Items per page (max 100)")
    ),
    security(("bearer_auth" = [])),
    responses(
        (status = 200, description = "List of friends with pagination", body = PaginatedResponse<UserProfile>),
        (status = 401, description = "Unauthorized")
    )
)]
#[get("/list")]
async fn get_friends_list(
    pool: web::Data<PgPool>,
    user: AuthenticatedUser,
    query: web::Query<UserSearchQuery>,
) -> impl Responder {
    match friend_service::get_friends_list(&pool, user.id, query.page, query.limit).await {
        Ok(friends) => HttpResponse::Ok().json(friends),
        Err(_) => HttpResponse::InternalServerError().finish(),
    }
}

#[utoipa::path(
    get,
    path = "/users/search",
    params(
        ("query" = String, Query, description = "Search query"),
        ("page" = Option<i32>, Query, description = "Page number (1-based)"),
        ("limit" = Option<i32>, Query, description = "Items per page (max 100)")
    ),
    security(("bearer_auth" = [])),
    responses(
        (status = 200, description = "Search results with friend status", body = PaginatedResponse<UserSearchResult>),
        (status = 401, description = "Unauthorized")
    )
)]
#[get("/users/search")]
async fn search_users(
    pool: web::Data<PgPool>,
    user: AuthenticatedUser,
    query: web::Query<UserSearchQuery>,
) -> impl Responder {
    match friend_service::search_users(&pool, user.id, &query.query, query.page, query.limit).await {
        Ok(users) => HttpResponse::Ok().json(users),
        Err(_) => HttpResponse::InternalServerError().finish(),
    }
}

pub fn init_friend_routes(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/friends")
            .service(send_friend_request)
            .service(accept_friend_request)
            .service(reject_friend_request)
            .service(cancel_friend_request)
            .service(get_friends_list)
    )
    .service(search_users);
} 