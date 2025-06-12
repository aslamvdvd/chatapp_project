use actix_web::{middleware::Logger, web::Data, App, HttpServer};
use dotenvy::dotenv;
use std::env;
use utoipa::{
    openapi::security::{Http, HttpAuthScheme, SecurityScheme},
    Modify, OpenApi,
};
use utoipa_swagger_ui::SwaggerUi;

// Module declarations
pub mod config;
pub mod core;
pub mod db;
pub mod handlers;
pub mod logging;
pub mod models;
pub mod routes;
pub mod services;
pub mod utils;

use crate::core::app_state::AppState;
use crate::core::feature_flags::FeatureFlags;
use crate::handlers::health_handler::{db_health_check, health_check};
use crate::logging::init_logging;
use crate::routes::admin_routes::configure_admin_routes;
use crate::routes::auth::configure_auth_routes;
use crate::services::auth_service::AuthService;

/// Main function to set up and run the Actix web server.
#[actix_web::main]
async fn main() -> std::io::Result<()> {
    dotenv().ok();
    let _ = init_logging();

    let db_pool = crate::db::create_pool()
        .await
        .expect("Failed to create database pool.");

    tracing::info!(target: "system_events", "Applying database migrations...");
    match sqlx::migrate!("./migrations").run(&db_pool).await {
        Ok(_) => {
            tracing::info!(target: "system_events", "Database migrations applied successfully.")
        }
        Err(e) => {
            tracing::error!(target: "system_events", error = %e, "Failed to apply database migrations.");
            // In production, you might want to exit here.
        }
    }

    let feature_flags = FeatureFlags::from_env();
    let app_state = AppState::new(db_pool.clone(), feature_flags.into());
    let auth_service_data = Data::new(AuthService::new(db_pool.clone()));

    #[derive(OpenApi)]
    #[openapi(
        paths(
            crate::handlers::auth_handler::signup_handler,
            crate::handlers::auth_handler::login_handler,
            crate::handlers::auth_handler::me_handler,
            crate::handlers::health_handler::health_check,
            crate::handlers::health_handler::db_health_check,
            crate::handlers::admin_handler::admin_root_handler
        ),
        components(
            schemas(
                crate::models::user::SignupUserDto,
                crate::models::user::UserPublicData,
                crate::models::user::UserInfoResponse,
                crate::handlers::auth_handler::ApiError,
                crate::core::rbac::Role,
                crate::models::auth::LoginRequest,
                crate::models::auth::LoginResponse
            )
        ),
        tags(
            (name = "auth", description = "Authentication endpoints"),
            (name = "health", description = "Health check endpoints"),
            (name = "admin", description = "Admin-only endpoints")
        ),
        modifiers(&SecurityAddon)
    )]
    struct ApiDoc;

    struct SecurityAddon;

    impl Modify for SecurityAddon {
        fn modify(&self, openapi: &mut utoipa::openapi::OpenApi) {
            let components = openapi.components.get_or_insert_with(Default::default);
            components.add_security_scheme(
                "bearer_auth",
                SecurityScheme::Http(Http::new(HttpAuthScheme::Bearer)),
            )
        }
    }

    let openapi = ApiDoc::openapi();

    let port: u16 = env::var("PORT")
        .unwrap_or_else(|_| "8080".to_string())
        .parse()
        .expect("PORT must be a valid u16");

    HttpServer::new(move || {
        App::new()
            .app_data(Data::new(app_state.clone()))
            .app_data(auth_service_data.clone())
            .wrap(Logger::default())
            .service(
                SwaggerUi::new("/swagger-ui/{_:.*}").url("/api-doc/openapi.json", openapi.clone()),
            )
            .configure(configure_auth_routes)
            .configure(configure_admin_routes)
            .service(health_check)
            .service(db_health_check)
    })
    .bind(("0.0.0.0", port))?
    .run()
    .await
}
