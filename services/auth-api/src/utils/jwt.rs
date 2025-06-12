// backend/src/utils/jwt.rs
// This file will handle JWT creation and validation logic. 

use chrono::{Duration, Utc};
use jsonwebtoken::{decode, encode, DecodingKey, EncodingKey, Header, Validation, Algorithm};
use serde::{Deserialize, Serialize};
use uuid::Uuid;
use std::env;
use thiserror::Error;

use crate::core::rbac::Role; // Assuming Role enum is in core::rbac

// --- Actix Web FromRequest Extractor for AuthenticatedUser ---
use actix_web::{Error as ActixWebError, FromRequest, HttpRequest};
use actix_web::dev::Payload;
use std::future::{ready, Ready};
use crate::handlers::auth_handler::ApiError; // To convert JwtError into an Actix compatible error

#[derive(Debug, Error)]
pub enum JwtError {
    #[error("Invalid token")]
    InvalidToken,
    #[error("Token creation error")]
    TokenCreation,
    #[error("Token expired")]
    TokenExpired,
    #[error("Missing environment variable: {0}")]
    MissingEnvVar(String),
    #[error("Invalid environment variable: {0}")]
    InvalidEnvVar(String),
    #[error("Internal JWT error: {0}")]
    Internal(String),
}

impl From<jsonwebtoken::errors::Error> for JwtError {
    fn from(err: jsonwebtoken::errors::Error) -> Self {
        match err.kind() {
            jsonwebtoken::errors::ErrorKind::InvalidToken => JwtError::InvalidToken,
            jsonwebtoken::errors::ErrorKind::InvalidSignature => JwtError::InvalidToken,
            jsonwebtoken::errors::ErrorKind::InvalidAlgorithm => JwtError::InvalidToken,
            jsonwebtoken::errors::ErrorKind::ExpiredSignature => JwtError::TokenExpired,
            jsonwebtoken::errors::ErrorKind::ImmatureSignature => JwtError::InvalidToken, // Token used before nbf
            jsonwebtoken::errors::ErrorKind::InvalidIssuer => JwtError::InvalidToken,
            jsonwebtoken::errors::ErrorKind::InvalidAudience => JwtError::InvalidToken,
            jsonwebtoken::errors::ErrorKind::InvalidSubject => JwtError::InvalidToken,
            _ => JwtError::Internal(err.to_string()),
        }
    }
}


/// JWT Claims structure.
#[derive(Debug, Serialize, Deserialize)]
pub struct Claims {
    /// Subject (user ID).
    pub sub: Uuid,
    /// User role.
    pub role: Role,
    /// Expiration timestamp (seconds since epoch).
    pub exp: usize,
    /// Issued at timestamp (seconds since epoch).
    pub iat: usize,
}

/// Generates a JWT for a given user ID and role.
///
/// The secret key and expiration time are read from environment variables:
/// - `JWT_SECRET`: The secret key used to sign the token.
/// - `JWT_EXPIRY_MINUTES`: The token's validity duration in minutes.
///
/// # Arguments
///
/// * `user_id` - The UUID of the user for whom the token is generated.
/// * `role` - The role of the user.
///
/// # Returns
///
/// A `Result` containing the JWT string on success, or a `JwtError` on failure.
pub fn generate_jwt(user_id: Uuid, role: Role) -> Result<String, JwtError> {
    let jwt_secret = env::var("JWT_SECRET")
        .map_err(|_| JwtError::MissingEnvVar("JWT_SECRET".to_string()))?;
    let jwt_expiry_minutes: i64 = env::var("JWT_EXPIRY_MINUTES")
        .map_err(|_| JwtError::MissingEnvVar("JWT_EXPIRY_MINUTES".to_string()))?
        .parse()
        .map_err(|_| JwtError::InvalidEnvVar("JWT_EXPIRY_MINUTES must be an integer".to_string()))?;

    let iat = Utc::now();
    let exp = iat + Duration::minutes(jwt_expiry_minutes);

    let claims = Claims {
        sub: user_id,
        role,
        iat: iat.timestamp() as usize,
        exp: exp.timestamp() as usize,
    };

    let header = Header::new(Algorithm::HS256);
    encode(&header, &claims, &EncodingKey::from_secret(jwt_secret.as_ref()))
        .map_err(|_| JwtError::TokenCreation)
}

/// Validates a JWT string and returns the decoded claims.
///
/// The secret key for validation is read from the `JWT_SECRET` environment variable.
///
/// # Arguments
///
/// * `token` - The JWT string to validate.
///
/// # Returns
///
/// A `Result` containing the decoded `Claims` on success, or a `JwtError` on failure.
pub fn validate_jwt(token: &str) -> Result<Claims, JwtError> {
    let jwt_secret = env::var("JWT_SECRET")
        .map_err(|_| JwtError::MissingEnvVar("JWT_SECRET".to_string()))?;
    
    let mut validation = Validation::new(Algorithm::HS256);
    validation.validate_exp = true; // Enable expiration check

    decode::<Claims>(token, &DecodingKey::from_secret(jwt_secret.as_ref()), &validation)
        .map(|data| data.claims)
        .map_err(Into::into)
}

// Basic structure for the AuthenticatedUser extractor (to be expanded later)
#[derive(Debug, Clone)]
pub struct AuthenticatedUser {
    pub user_id: Uuid,
    pub role: Role,
}

// --- Actix Web FromRequest Extractor for AuthenticatedUser ---
impl FromRequest for AuthenticatedUser {
    type Error = ActixWebError; // ApiError implements ResponseError, which can be converted to ActixWebError
    type Future = Ready<Result<Self, Self::Error>>;

    fn from_request(req: &HttpRequest, _payload: &mut Payload) -> Self::Future {
        // 1. Extract Authorization header
        let auth_header = match req.headers().get("Authorization") {
            Some(header) => header,
            None => {
                let api_error = ApiError {
                    status_code: 401,
                    message: "Missing Authorization header.".to_string(),
                    errors: None,
                };
                return ready(Err(api_error.into()));
            }
        };

        // 2. Parse "Bearer <token>" format
        let auth_str = match auth_header.to_str() {
            Ok(s) => s,
            Err(_) => {
                let api_error = ApiError {
                    status_code: 400, // Bad Request for malformed header value
                    message: "Authorization header contains invalid characters.".to_string(),
                    errors: None,
                };
                return ready(Err(api_error.into()));
            }
        };

        if !auth_str.starts_with("Bearer ") {
            let api_error = ApiError {
                status_code: 401,
                message: "Invalid token format. Expected 'Bearer <token>'.".to_string(),
                errors: None,
            };
            return ready(Err(api_error.into()));
        }

        let token = &auth_str["Bearer ".len()..];

        // 3. Validate JWT
        match validate_jwt(token) {
            Ok(claims) => {
                ready(Ok(AuthenticatedUser {
                    user_id: claims.sub,
                    role: claims.role,
                }))
            }
            Err(jwt_error) => {
                // Convert JwtError to ApiError, then into ActixWebError
                let api_error: ApiError = jwt_error.into();
                ready(Err(api_error.into()))
            }
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::thread;
    use std::time::Duration as StdDuration; // Alias to avoid conflict with chrono::Duration
    use crate::core::rbac::Role; // Make sure Role is accessible for tests

    fn setup_env_vars() {
        env::set_var("JWT_SECRET", "test_secret_key_longer_than_16_bytes");
        env::set_var("JWT_EXPIRY_MINUTES", "1");
    }

    #[test]
    fn test_generate_and_validate_jwt_ok() {
        setup_env_vars();
        let user_id = Uuid::new_v4();
        let role = Role::User;

        let token = generate_jwt(user_id, role.clone()).expect("Failed to generate token");
        
        let claims = validate_jwt(&token).expect("Failed to validate token");

        assert_eq!(claims.sub, user_id);
        assert_eq!(claims.role, role);
        assert!(claims.exp > claims.iat);
    }

    #[test]
    fn test_validate_jwt_expired() {
        setup_env_vars();
        // Set a very short expiry for testing purposes
        env::set_var("JWT_EXPIRY_MINUTES", "0"); // Effectively expires immediately for practical test purposes
                                                // or use a custom generation with tiny positive expiry and sleep.

        let user_id = Uuid::new_v4();
        let role = Role::User;
        
        // Generate a token that will expire very quickly
        let iat = Utc::now();
        let exp_time = iat + Duration::nanoseconds(1); //ほぼ即時失効

        let claims_expired = Claims {
            sub: user_id,
            role,
            iat: iat.timestamp() as usize,
            exp: exp_time.timestamp() as usize,
        };
        
        let header = Header::new(Algorithm::HS256);
        let secret = env::var("JWT_SECRET").unwrap();
        let token = encode(&header, &claims_expired, &EncodingKey::from_secret(secret.as_ref()))
            .expect("Failed to generate expired token");

        // Allow a moment for the token to be considered expired
        thread::sleep(StdDuration::from_millis(50)); 

        let result = validate_jwt(&token);
        match result {
            Err(JwtError::TokenExpired) => (), // Expected
            Err(e) => panic!("Expected TokenExpired error, got {:?}", e),
            Ok(_) => panic!("Token validation should have failed due to expiration"),
        }
        
        // Reset env var for other tests if any
        env::set_var("JWT_EXPIRY_MINUTES", "1"); 
    }

    #[test]
    fn test_validate_jwt_invalid_secret() {
        setup_env_vars(); // Uses "test_secret_key_longer_than_16_bytes"
        let user_id = Uuid::new_v4();
        let role = Role::User;

        let token = generate_jwt(user_id, role).expect("Failed to generate token");

        // Change the secret for validation
        env::set_var("JWT_SECRET", "wrong_secret_key_for_sure_test");
        let result = validate_jwt(&token);
        match result {
            Err(JwtError::InvalidToken) => (), // Expected, as signature verification will fail
            Err(e) => panic!("Expected InvalidToken error due to wrong secret, got {:?}", e),
            Ok(_) => panic!("Token validation should have failed due to wrong secret"),
        }
        // Reset for other tests
        setup_env_vars();
    }

    #[test]
    fn test_generate_jwt_missing_secret_env() {
        env::remove_var("JWT_SECRET");
        env::set_var("JWT_EXPIRY_MINUTES", "1");
        let user_id = Uuid::new_v4();
        let role = Role::User;
        let result = generate_jwt(user_id, role);
        match result {
            Err(JwtError::MissingEnvVar(var)) if var == "JWT_SECRET" => (),
            _ => panic!("Expected MissingEnvVar for JWT_SECRET"),
        }
        setup_env_vars(); // Restore
    }

    #[test]
    fn test_generate_jwt_missing_expiry_env() {
        env::set_var("JWT_SECRET", "test_secret");
        env::remove_var("JWT_EXPIRY_MINUTES");
        let user_id = Uuid::new_v4();
        let role = Role::User;
        let result = generate_jwt(user_id, role);
        match result {
            Err(JwtError::MissingEnvVar(var)) if var == "JWT_EXPIRY_MINUTES" => (),
            _ => panic!("Expected MissingEnvVar for JWT_EXPIRY_MINUTES"),
        }
        setup_env_vars(); // Restore
    }

    #[test]
    fn test_generate_jwt_invalid_expiry_env() {
        env::set_var("JWT_SECRET", "test_secret");
        env::set_var("JWT_EXPIRY_MINUTES", "not_a_number");
        let user_id = Uuid::new_v4();
        let role = Role::User;
        let result = generate_jwt(user_id, role);
        match result {
            Err(JwtError::InvalidEnvVar(var)) if var.contains("JWT_EXPIRY_MINUTES") => (),
            _ => panic!("Expected InvalidEnvVar for JWT_EXPIRY_MINUTES"),
        }
        setup_env_vars(); // Restore
    }
} 