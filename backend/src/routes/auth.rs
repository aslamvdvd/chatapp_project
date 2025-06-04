use crate::handlers::auth_handler::signup_handler;
use actix_web::web;

/// Configures authentication routes.
///
/// This function is called by `main.rs` to set up all routes under the `/auth` prefix.
///
/// # Arguments
/// * `cfg` - A mutable reference to Actix `web::ServiceConfig`.
pub fn configure_auth_routes(cfg: &mut web::ServiceConfig) {
    cfg.service(
        web::scope("/auth").route("/signup", web::post().to(signup_handler)), // TODO: Add routes for /login, /logout, /refresh-token, /request-password-reset, /reset-password etc.
    );
}
