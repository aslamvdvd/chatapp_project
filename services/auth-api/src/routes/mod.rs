pub mod auth_routes;
pub mod friend_routes;

use actix_web::web;

pub fn init_routes(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("")
            .configure(auth_routes::configure)
            .configure(friend_routes::configure)
    );
}
