use actix_web::{App, HttpServer, middleware::Logger};
use std::io::Result;

// Configuration module (e.g., for loading settings)
pub mod config;

// Handlers module (for request handling logic)
pub mod handlers;

// Routes module (for defining API routes)
pub mod routes;

#[actix_web::main]
async fn main() -> Result<()> {
    // Initialize logger
    env_logger::init_from_env(env_logger::Env::default().default_filter_or("info"));

    // Load configuration
    let app_config = match config::Config::from_env() {
        Ok(cfg) => cfg,
        Err(e) => {
            log::error!("Failed to load configuration: {}", e);
            std::process::exit(1);
        }
    };

    let server_address = app_config.server_address.clone();

    log::info!("Starting server at http://{}", server_address);

    HttpServer::new(move || {
        App::new()
            .wrap(Logger::default()) // Enable default request logging
            .configure(routes::configure_routes) // Configure routes
    })
    .bind(&server_address)?
    .run()
    .await
}
