use actix_web::web;
use crate::handlers::health_check;

pub fn configure_routes(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/api") // Prefixes all routes with /api
            .service(health_check)
    );
} 