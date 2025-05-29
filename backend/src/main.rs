use actix_web::{web::{self, Data}, App, HttpServer, Responder, HttpResponse};
use dotenvy::dotenv;
use std::env;

// Modules
pub mod db;
pub mod handlers;
pub mod models;
pub mod routes;
pub mod services;
pub mod utils;
pub mod logging;

// Use statements for services and routes
use crate::services::auth_service::AuthService;
use crate::routes::auth::configure_auth_routes;
use crate::logging::init_logging;

// For OpenAPI/Swagger documentation
use utoipa::OpenApi;
use utoipa_swagger_ui::SwaggerUi;

/// Application state, shared across all handlers.
/// Currently empty, but can hold shared resources like a config struct if needed later.
pub struct AppState {
    // auth_service: AuthService, // Removed as AuthService is injected directly via Data<AuthService>
    // TODO: Add other shared resources like app configuration if needed.
}

/// Basic health check endpoint.
async fn health_check() -> impl Responder {
    HttpResponse::Ok().body("chatapp_by_aarchangel backend is healthy!")
}

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
            return Err(std::io::Error::new(std::io::ErrorKind::Other, e.to_string()));
        }
    };

    // Run SQLx migrations
    tracing::info!(target: "system_events", "Applying database migrations...");
    match sqlx::migrate!("./migrations") // Path relative to CARGO_MANIFEST_DIR
        .run(&db_pool)
        .await
    {
        Ok(_) => tracing::info!(target: "system_events", "Database migrations applied successfully."),
        Err(e) => {
            tracing::error!(target: "system_events", error = %e, "Failed to apply database migrations.");
            // Depending on the error, you might want to exit here.
            // For now, we log and continue, but in production, this could be a fatal error.
        }
    }

    // Initialize services
    let auth_service_data = Data::new(AuthService::new(db_pool.clone()));

    // Application state (now minimal or potentially empty if no other shared state)
    let app_state_data = Data::new(AppState {}); // Create an empty AppState for now

    // OpenAPI documentation setup
    #[derive(OpenApi)]
    #[openapi(
        paths(
            handlers::auth_handler::signup_handler,
            // TODO: Add other handlers here as they are created
        ),
        components(
            schemas(models::user::SignupUserDto, models::user::UserPublicData, handlers::auth_handler::ApiError)
            // TODO: Add other DTOs/models here
        ),
        tags(
            (name = "chatapp_by_aarchangel - Auth", description = "Authentication endpoints")
        ),
        info(
            title = "chatapp_by_aarchangel API",
            version = "0.1.0",
            description = "API for chatapp_by_aarchangel",
            contact(
                name = "aarchangel Support",
                email = "support@chatapp.aarchangel.example.com"
            )
        )
    )]
    struct ApiDoc;

    let openapi = ApiDoc::openapi();

    let host = env::var("HOST").unwrap_or_else(|_| "127.0.0.1".to_string());
    let port = env::var("PORT").unwrap_or_else(|_| "8080".to_string()).parse::<u16>()
        .map_err(|e| std::io::Error::new(std::io::ErrorKind::InvalidInput, format!("Invalid PORT number: {}", e)))?;

    tracing::info!(target: "system_events", host = %host, port = %port, "Server starting...");
    tracing::info!(target: "system_events", swagger_ui_path = format!("http://{}:{}/swagger-ui/", host, port), "Swagger UI available for chatapp_by_aarchangel API");

    HttpServer::new(move || {
        App::new()
            .app_data(app_state_data.clone()) // Share AppState (even if empty)
            .app_data(auth_service_data.clone()) // Share AuthService directly
            .wrap(tracing_actix_web::TracingLogger::default()) // Add TracingLogger middleware
            .configure(configure_auth_routes) // Configure auth routes
            .service(web::resource("/health").route(web::get().to(health_check))) // Health check
            .service(
                SwaggerUi::new("/swagger-ui/{_:.*}")
                    .url("/api-doc/openapi.json", openapi.clone()),
            )
            // TODO: Add other route configurations here
    })
    .bind((host, port))?
    .run()
    .await
}
