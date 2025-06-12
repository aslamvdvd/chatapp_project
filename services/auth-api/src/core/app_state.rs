use crate::core::feature_flags::FeatureFlags;
use sqlx::PgPool;
use std::sync::Arc; // Will be created later
                    // use tracing::subscriber::DefaultGuard; // If we store the global subscriber guard

/// `AppState` holds the shared state for the application.
///
/// This includes database connections, configuration, feature flags,
/// and potentially other shared resources like a cache client or a global logger instance.
#[derive(Clone)] // Clone is often useful for Actix Data<AppState>
pub struct AppState {
    pub db_pool: PgPool,
    pub feature_flags: Arc<FeatureFlags>, // Feature flags will be Arc-wrapped for shared immutable access
                                          // pub logger_guard: Option<Arc<DefaultGuard>>, // Optional: if we need to manage logger guard lifetime explicitly
                                          // pub config: Arc<AppConfig>, // Placeholder for a dedicated config struct
                                          // pub cache_client: Option<Arc<RedisPool>>, // Placeholder for a cache client
}

impl AppState {
    /// Creates a new `AppState` instance.
    ///
    /// # Arguments
    /// * `db_pool` - A `PgPool` for database connections.
    /// * `feature_flags` - An `Arc<FeatureFlags>` instance.
    pub fn new(db_pool: PgPool, feature_flags: Arc<FeatureFlags>) -> Self {
        Self {
            db_pool,
            feature_flags,
            // logger_guard: None,
            // config: Arc::new(AppConfig::load_from_env().unwrap_or_default()), // Example
        }
    }

    // Example helper method (can be expanded)
    // pub fn get_db_pool(&self) -> &PgPool {
    //     &self.db_pool
    // }
}

// Placeholder for AppConfig if you want to centralize other configs beyond .env directly
// #[derive(Clone, Debug, serde::Deserialize)]
// pub struct AppConfig {
//     pub some_setting: String,
//     // ... other config fields
// }
//
// impl AppConfig {
//     pub fn load_from_env() -> Result<Self, envy::Error> {
//         envy::from_env::<AppConfig>()
//     }
// }
//
// impl Default for AppConfig { // For unwrap_or_default
//     fn default() -> Self {
//         // Sensible defaults if .env loading fails or for testing
//         Self { some_setting: "default_value".to_string() }
//     }
// }
