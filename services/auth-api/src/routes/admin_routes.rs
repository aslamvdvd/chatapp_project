use actix_web::web;
use crate::handlers::admin_handler::admin_root_handler;

/// Configures admin-specific routes.
///
/// This function is called by `main.rs` to set up all routes under the `/admin` prefix.
///
/// # Arguments
/// * `cfg` - A mutable reference to Actix `web::ServiceConfig`.
pub fn configure_admin_routes(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/admin")
            .route("", web::get().to(admin_root_handler)) // Route for /admin
        // TODO: Add more admin-specific routes here
        // e.g., .route("/users", web::get().to(get_all_users_handler))
    );
} 