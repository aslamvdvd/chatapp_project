// Placeholder for db.rs (PostgreSQL pool setup)

use sqlx::postgres::{PgPool, PgPoolOptions};
use std::env;

/// Establishes a connection pool to the PostgreSQL database.
///
/// Reads the `DATABASE_URL` from environment variables to configure the connection.
///
/// # Panics
/// Panics if `DATABASE_URL` is not set or if the connection pool cannot be created.
/// This is intentional for application startup: if the DB isn't available, the app can't run.
pub async fn create_pool() -> Result<PgPool, sqlx::Error> {
    let database_url = env::var("DATABASE_URL").expect("DATABASE_URL must be set in .env file");

    // TODO: Make pool size configurable via environment variables
    PgPoolOptions::new()
        .max_connections(10) // Default good for many apps, but can be tuned
        .connect(&database_url)
        .await
}
