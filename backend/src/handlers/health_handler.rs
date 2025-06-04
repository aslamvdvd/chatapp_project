use actix_web::{web::Data, HttpResponse, Responder};
use serde_json::json;
use utoipa; // Required for the path macro attribute

use crate::core::app_state::AppState; // For db_health_check
use sqlx; // For db_health_check
use tracing; // For logging in db_health_check

#[utoipa::path(
    get,
    path = "/health",
    tag = "Health",
    responses(
        (status = 200, description = "Backend is healthy", body = String, example = json!("chatapp_by_aarchangel backend is healthy!"))
    )
)]
/// Basic health check endpoint.
pub async fn health_check() -> impl Responder {
    HttpResponse::Ok().body("chatapp_by_aarchangel backend is healthy!")
}

#[utoipa::path(
    get,
    path = "/health/db",
    tag = "Health",
    responses(
        (status = 200, description = "Database connection is healthy", body = String, example = json!("Database connection is healthy.")),
        (status = 503, description = "Database connection is unhealthy", body = String, example = json!("Database connection failed."))
    )
)]
/// Specific health check for the database connection.
pub async fn db_health_check(app_state: Data<AppState>) -> impl Responder {
    match sqlx::query("SELECT 1")
        .fetch_one(&app_state.db_pool) // Ping the database
        .await
    {
        Ok(_) => {
            tracing::info!(target: "system_events", "Database health check: OK");
            HttpResponse::Ok().json("Database connection is healthy.")
        }
        Err(e) => {
            tracing::error!(target: "system_events", error = %e, "Database health check: FAILED");
            HttpResponse::ServiceUnavailable().json(format!("Database connection failed: {}", e))
        }
    }
} 