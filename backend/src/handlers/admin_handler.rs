use actix_web::{HttpResponse, Responder};
use serde_json::json;
use utoipa;

// TODO: Add RBAC checks to admin handlers using `crate::core::rbac::require_role`
// and an `AuthenticatedUser` extractor once implemented.

#[utoipa::path(
    get,
    path = "/admin",
    tag = "Admin", // We'll need to add this tag to ApiDoc in main.rs
    responses(
        (status = 200, description = "Admin section root", body = String, example = json!("Welcome to the Admin Dashboard!"))
        // TODO: Add 401/403 responses once auth is integrated
    )
)]
/// Placeholder handler for the admin root.
pub async fn admin_root_handler() -> impl Responder {
    // Placeholder: In a real app, this would check for Admin role
    HttpResponse::Ok().json("Welcome to the Admin Dashboard!")
}

// TODO: Add other admin-specific handlers here, e.g.:
// - Get all users
// - Get server stats
// - Manage feature flags (if dynamic) 