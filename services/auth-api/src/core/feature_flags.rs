// use serde::Deserialize; // Removed unused import
use std::env;
use std::str::FromStr;

/// Defines available feature flags in the application.
#[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
pub enum Feature {
    SignupEnabled,
    LoginEnabled, // Added for login feature
    LoggingVerbose, // Example: For more detailed logging if enabled
    EnableExperimentalApi, // Example: To toggle a new set of APIs
                    // Add more features as needed
}

impl FromStr for Feature {
    type Err = String;

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        match s.to_lowercase().as_str() {
            "signupenabled" => Ok(Feature::SignupEnabled),
            "loginenabled" => Ok(Feature::LoginEnabled), // Added for login
            "loggingverbose" => Ok(Feature::LoggingVerbose),
            "enableexperimentalapi" => Ok(Feature::EnableExperimentalApi),
            _ => Err(format!("Unknown feature: {}", s)),
        }
    }
}

/// `FeatureFlags` struct holds the state of all feature flags.
///
/// It reads environment variables at startup to determine which features are enabled.
/// Expected environment variable format: `FEATURE_SIGNUP_ENABLED=true`
#[derive(Debug, Clone)]
pub struct FeatureFlags {
    // Using specific fields for type safety and direct access
    pub signup_enabled: bool,
    pub login_enabled: bool, // Added for login
    pub logging_verbose: bool,
    pub enable_experimental_api: bool,
    // Add fields for other features
}

impl FeatureFlags {
    /// Loads feature flag states from environment variables.
    ///
    /// Defaults to `false` if an environment variable is not set or parsing fails,
    /// except for critical features like `SignupEnabled` which defaults to `true` for now.
    pub fn from_env() -> Self {
        let signup_enabled = env::var("FEATURE_SIGNUP_ENABLED")
            .ok()
            .and_then(|val| val.parse::<bool>().ok())
            .unwrap_or(true); // Default to true for signup for now

        let login_enabled = env::var("FEATURE_LOGIN_ENABLED")
            .ok()
            .and_then(|val| val.parse::<bool>().ok())
            .unwrap_or(true); // Default to true for login for now

        let logging_verbose = env::var("FEATURE_LOGGING_VERBOSE")
            .ok()
            .and_then(|val| val.parse::<bool>().ok())
            .unwrap_or(false);

        let enable_experimental_api = env::var("FEATURE_ENABLE_EXPERIMENTAL_API")
            .ok()
            .and_then(|val| val.parse::<bool>().ok())
            .unwrap_or(false);

        tracing::info!(
            target: "system_events",
            feature_signup_enabled = signup_enabled,
            feature_login_enabled = login_enabled, // Added for login
            feature_logging_verbose = logging_verbose,
            feature_enable_experimental_api = enable_experimental_api,
            "Feature flags loaded"
        );

        Self {
            signup_enabled,
            login_enabled, // Added for login
            logging_verbose,
            enable_experimental_api,
        }
    }

    /// Checks if a specific feature is enabled.
    /// This provides a more generic way to check flags if needed, though direct field access is often clearer.
    pub fn is_enabled(&self, feature: Feature) -> bool {
        match feature {
            Feature::SignupEnabled => self.signup_enabled,
            Feature::LoginEnabled => self.login_enabled, // Added for login
            Feature::LoggingVerbose => self.logging_verbose,
            Feature::EnableExperimentalApi => self.enable_experimental_api,
        }
    }
}

/*
Expected .env keys for feature flags:

FEATURE_SIGNUP_ENABLED=true
FEATURE_LOGIN_ENABLED=true
FEATURE_LOGGING_VERBOSE=false
FEATURE_ENABLE_EXPERIMENTAL_API=false

*/
