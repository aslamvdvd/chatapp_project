use actix_web::web;
use crate::handlers::friend_handler;

pub fn configure(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/friends")
            .route("/request", web::post().to(friend_handler::send_request))
            .route("/accept", web::post().to(friend_handler::accept_request))
            .route("/reject", web::post().to(friend_handler::reject_request))
            .route("/cancel", web::post().to(friend_handler::cancel_request))
            .route("/list", web::get().to(friend_handler::list_friends))
            .route("/requests", web::get().to(friend_handler::list_requests))
    )
    .service(
        web::scope("/users")
            .route("/search", web::get().to(friend_handler::search_users))
    );
} 