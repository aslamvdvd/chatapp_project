use actix_web::web;
use crate::handlers::auth_handler;

pub fn configure(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/auth")
            .route("/signup", web::post().to(auth_handler::signup_handler))
            .route("/login", web::post().to(auth_handler::login_handler))
            .route("/me", web::get().to(auth_handler::me_handler))
    );
} 