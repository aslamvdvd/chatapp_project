use actix_web::{
    web::{self, Data},
    App, HttpResponse, HttpServer, Responder,
};
use dotenvy::dotenv;
use std::env;
use std::sync::Arc;

// Modules
pub mod core;
pub mod db;
pub mod handlers;
pub mod logging;
pub mod models;
pub mod routes;
pub mod services;
pub mod utils;

// Use statements for services and routes
use crate::core::app_state::AppState;
use crate::core::feature_flags::FeatureFlags;
use crate::logging::init_logging;
use crate::routes::auth::configure_auth_routes;
use crate::routes::admin_routes::configure_admin_routes;
use crate::services::auth_service::AuthService;
use crate::handlers::health_handler::{health_check, db_health_check};

// For OpenAPI/Swagger documentation
use utoipa::OpenApi;
use utoipa_swagger_ui::SwaggerUi;

/// Main function to set up and run the Actix web server.
#[actix_web::main]
async fn main() -> std::io::Result<()> {
    // Load environment variables from .env file
    dotenv().ok();

    // Initialize logging FIRST
    let (_system_log_guard, _user_log_guard, _admin_log_guard) = init_logging()
        .expect("Failed to initialize logging. Ensure 'logs' directory can be created.");

    // Replace previous println! with tracing::info!
    tracing::info!(target: "system_events", "Starting chatapp_by_aarchangel backend server...");

    // Create database connection pool
    let db_pool = match db::create_pool().await {
        Ok(pool) => {
            tracing::info!(target: "system_events", "Database pool created successfully.");
            pool
        }
        Err(e) => {
            tracing::error!(target: "system_events", error = %e, "Failed to create database pool. Ensure DB is running and DATABASE_URL is set.");
            return Err(std::io::Error::new(
                std::io::ErrorKind::Other,
                e.to_string(),
            ));
        }
    };

    // Initialize FeatureFlags (will be defined in the next step)
    let feature_flags = Arc::new(FeatureFlags::from_env());

    // Run SQLx migrations
    tracing::info!(target: "system_events", "Applying database migrations...");
    match sqlx::migrate!("./migrations") // Path relative to CARGO_MANIFEST_DIR
        .run(&db_pool)
        .await
    {
        Ok(_) => {
            tracing::info!(target: "system_events", "Database migrations applied successfully.")
        }
        Err(e) => {
            tracing::error!(target: "system_events", error = %e, "Failed to apply database migrations.");
            // Depending on the error, you might want to exit here.
            // For now, we log and continue, but in production, this could be a fatal error.
        }
    }

    // Create the new AppState
    let app_state = Data::new(AppState::new(db_pool.clone(), feature_flags.clone()));

    // Initialize services - they might take AppState or specific parts like PgPool
    // For now, AuthService takes PgPool directly. If it needed AppState, it would be passed here.
    let auth_service_data = Data::new(AuthService::new(db_pool.clone())); // AuthService still takes PgPool directly

    // OpenAPI documentation setup
    #[derive(OpenApi)]
    #[openapi(
        paths(
            crate::handlers::auth_handler::signup_handler,
            crate::handlers::health_handler::health_check,
            crate::handlers::health_handler::db_health_check,
            crate::handlers::admin_handler::admin_root_handler
        ),
        components(
            schemas(crate::models::user::SignupUserDto, crate::models::user::UserPublicData, crate::handlers::auth_handler::ApiError, crate::core::rbac::Role)
        ),
        tags(
            (name = "chatapp_by_aarchangel_backend", description = "ChatApp by aarchangel - Backend API"),
            (name = "Health", description = "Health Check Operations"),
            (name = "Admin", description = "Admin Operations")
        ),
        info(
            title = "ChatApp by aarchangel - Backend API",
            version = "0.1.0",
            description = "API for GhostTalk (ChatApp by aarchangel)",
            contact(
                name = "Support",
                email = "support@example.com"
            )
        )
    )]
    struct ApiDoc;

    let openapi = ApiDoc::openapi();

    let host = env::var("HOST").unwrap_or_else(|_| "0.0.0.0".to_string());
    let port = env::var("PORT")
        .unwrap_or_else(|_| "8080".to_string())
        .parse::<u16>()
        .map_err(|e| {
            std::io::Error::new(
                std::io::ErrorKind::InvalidInput,
                format!("Invalid PORT number: {}", e),
            )
        })?;

    tracing::info!(target: "system_events", host = %host, port = %port, "Server starting...");
    tracing::info!(target: "system_events", swagger_ui_path = format!("http://{}:{}/swagger-ui/", host, port), "Swagger UI available for chatapp_by_aarchangel API");

    HttpServer::new(move || {
        App::new()
            .app_data(app_state.clone()) // Pass the new AppState
            .app_data(auth_service_data.clone()) // AuthService still passed directly for now
            // If AuthService used AppState, this direct injection might be removed
            // or AuthService could be retrieved from AppState in handlers.
            .wrap(tracing_actix_web::TracingLogger::default())
            .configure(configure_auth_routes)
            .configure(configure_admin_routes)
            .service(web::resource("/health").route(web::get().to(health_check)))
            .service(web::resource("/health/db").route(web::get().to(db_health_check)))
            .service(
                SwaggerUi::new("/swagger-ui/{_:.*}").url("/api-doc/openapi.json", openapi.clone()),
            )
    })
    .bind((host, port))?
    .run()
    .await
}
