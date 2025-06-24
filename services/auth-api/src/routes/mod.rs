pub mod auth;
pub mod friends;

use actix_web::web;

pub fn init_routes(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("")
            .configure(auth::init_auth_routes)
            .configure(friends::init_friend_routes)
    );
}
