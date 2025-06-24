use actix_web::{web, HttpResponse, Responder, get, post};
use crate::models::friends::{FriendRequestPayload, AcceptRequestPayload, UserSearchQuery};
use crate::services::friend_service;
use crate::db::PgPool;
use crate::core::rbac::AuthenticatedUser;

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

#[post("/accept")]
async fn accept_friend_request(
    pool: web::Data<PgPool>,
    user: AuthenticatedUser,
    payload: web::Json<AcceptRequestPayload>,
) -> impl Responder {
    match friend_service::accept_friend_request(&pool, payload.request_id, user.id).await {
        Ok(_) => HttpResponse::Ok().finish(),
        Err(_) => HttpResponse::InternalServerError().finish(),
    }
}

#[get("/list")]
async fn get_friends_list(
    pool: web::Data<PgPool>,
    user: AuthenticatedUser,
) -> impl Responder {
    match friend_service::get_friends_list(&pool, user.id).await {
        Ok(friends) => HttpResponse::Ok().json(friends),
        Err(_) => HttpResponse::InternalServerError().finish(),
    }
}

#[get("/users/search")]
async fn search_users(
    pool: web::Data<PgPool>,
    query: web::Query<UserSearchQuery>,
) -> impl Responder {
    match friend_service::search_users(&pool, &query.query).await {
        Ok(users) => HttpResponse::Ok().json(users),
        Err(_) => HttpResponse::InternalServerError().finish(),
    }
}

pub fn init_friend_routes(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/friends")
            .service(send_friend_request)
            .service(accept_friend_request)
            .service(get_friends_list)
    )
    .service(search_users);
} 