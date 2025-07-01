use actix_web::{middleware::Logger, web::Data, App, HttpServer, web, error, HttpRequest};
use dotenv::dotenv;
use sqlx::postgres::PgPoolOptions;
use tracing::info;
use utoipa::OpenApi;
use utoipa_swagger_ui::SwaggerUi;
use crate::{
    core::{app_state::AppState, feature_flags::FeatureFlags},
    middleware::rate_limiter::RateLimiter,
    services::auth_service::AuthService,
    handlers::auth_handler::ApiError,
};

mod config;
mod core;
mod handlers;
mod middleware;
mod models;
mod routes;
mod services;
mod utils;

#[derive(OpenApi)]
#[openapi(
    paths(
        handlers::auth_handler::signup_handler,
        handlers::auth_handler::login_handler,
        handlers::auth_handler::me_handler,
        handlers::health_handler::health_check,
        handlers::health_handler::db_health_check,
        handlers::admin_handler::admin_root_handler,
        handlers::friend_handler::send_request,
        handlers::friend_handler::accept_request,
        handlers::friend_handler::reject_request,
        handlers::friend_handler::cancel_request,
        handlers::friend_handler::list_friends,
        handlers::friend_handler::search_users,
    ),
    components(
        schemas(
            models::user::SignupUserDto,
            models::user::UserPublicData,
            models::user::UserInfoResponse,
            models::auth::LoginRequest,
            models::auth::LoginResponse,
            models::friends::FriendRequest,
            models::friends::Friend,
            models::friends::FriendStatus,
            models::friends::FriendRequestPayload,
            models::friends::AcceptRequestPayload,
            models::friends::RejectRequestPayload,
            models::friends::CancelRequestPayload,
            models::friends::UserSearchResult,
            models::friends::PaginatedResponse<models::friends::UserSearchResult>,
            models::friends::PaginationInfo,
            core::rbac::Role,
            handlers::auth_handler::ApiError,
        )
    ),
    tags(
        (name = "auth-api", description = "User authentication and management endpoints")
    ),
    security(
        (), // No global security schemes
        ("bearer_auth" = [])
    )
)]
struct ApiDoc;

/// Custom error handler for JSON deserialization errors.
fn json_error_handler(err: error::JsonPayloadError, _req: &HttpRequest) -> error::Error {
    let detail = err.to_string();
    let mut errors = std::collections::HashMap::new();
    errors.insert("payload".to_string(), vec![detail]);

    let api_error = ApiError {
        status_code: 400,
        message: "Invalid JSON payload provided.".to_string(),
        errors: Some(errors),
    };

    api_error.into()
}

/// Main function to set up and run the Actix web server.
#[actix_web::main]
async fn main() -> std::io::Result<()> {
    // Initialize environment
    dotenv().ok();
    
    // Initialize tracing
    if std::env::var_os("RUST_LOG").is_none() {
        std::env::set_var("RUST_LOG", "info");
    }
    tracing_subscriber::fmt::init();

    // Load config
    let config = config::Config::from_env().expect("Failed to load config");
    
    // Initialize database
    let database_url = std::env::var("DATABASE_URL").expect("DATABASE_URL must be set");
    let pool = PgPoolOptions::new()
        .max_connections(5)
        .connect(&database_url)
        .await
        .expect("Failed to create pool");

    // Initialize Redis for rate limiting
    let redis_url = std::env::var("REDIS_URL").expect("REDIS_URL must be set");
    let rate_limiter = RateLimiter::new(&redis_url, 5)
        .expect("Failed to create rate limiter");

    // Initialize services
    let auth_service = AuthService::new(pool.clone());

    // Initialize feature flags and app state
    let feature_flags = FeatureFlags::from_env();
    let app_state = AppState::new(pool.clone(), std::sync::Arc::new(feature_flags));
    
    info!("Starting server at {}:{}", config.server_address, config.port);
    
    let openapi = ApiDoc::openapi();
    
    HttpServer::new(move || {
        App::new()
            .wrap(Logger::default())
            .wrap(rate_limiter.clone())
            .app_data(Data::new(app_state.clone()))
            .app_data(Data::new(auth_service.clone()))
            .app_data(web::JsonConfig::default().error_handler(json_error_handler))
            // Move SwaggerUi registration before configuring other routes
            .service(
                SwaggerUi::new("/swagger-ui/{_:.*}")
                    .url("/api-doc/openapi.json", openapi.clone()),
            )
            .configure(routes::init_routes) // Now, other routes are configured after Swagger UI
    })
    .bind((config.server_address.clone(), config.port))?
    .run()
    .await
}
