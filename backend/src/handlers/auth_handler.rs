use crate::core::app_state::AppState;
use crate::models::user::SignupUserDto;
use crate::models::auth::LoginRequest;
use crate::services::auth_service::{AuthService, AuthServiceError};
use actix_web::{
    web::{Data, Json},
    HttpResponse, ResponseError,
};
use serde::Serialize;
use std::collections::HashMap;
use utoipa::ToSchema;
use validator::Validate;

// Import JwtError to implement From trait
use crate::utils::jwt::JwtError;

/// Represents errors that can occur in the auth handlers.
/// Implements `ResponseError` to convert into HTTP responses.
#[derive(Debug, Serialize, ToSchema)]
#[schema(example = json!({
    "status_code": 400,
    "message": "Input validation failed.",
    "errors": {
        "email": ["Email must be a valid email address."],
        "password": ["Password must be at least 8 characters long."]
    }
}))]
pub struct ApiError {
    pub status_code: u16,
    pub message: String,
    #[schema(value_type = Option<HashMap<String, Vec<String>>>)]
    pub errors: Option<HashMap<String, Vec<String>>>,
}

impl std::fmt::Display for ApiError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.message)
    }
}

impl ResponseError for ApiError {
    fn status_code(&self) -> actix_web::http::StatusCode {
        actix_web::http::StatusCode::from_u16(self.status_code)
            .unwrap_or(actix_web::http::StatusCode::INTERNAL_SERVER_ERROR)
    }

    fn error_response(&self) -> HttpResponse {
        let mut response = match self.status_code {
            400 => HttpResponse::BadRequest(),
            403 => HttpResponse::Forbidden(),
            409 => HttpResponse::Conflict(),
            500 => HttpResponse::InternalServerError(),
            _ => HttpResponse::InternalServerError(),
        };
        response.json(self)
    }
}

impl From<AuthServiceError> for ApiError {
    fn from(err: AuthServiceError) -> Self {
        match err {
            AuthServiceError::Validation(details) => ApiError {
                status_code: 400,
                message: "Input validation failed.".to_string(),
                errors: Some(details),
            },
            AuthServiceError::Database(e) => {
                tracing::error!(target: "system_events", error = %e, "Database error in auth service.");
                ApiError {
                    status_code: 500,
                    message: "An internal database error occurred.".to_string(),
                    errors: None,
                }
            }
            AuthServiceError::PasswordHashing(msg) => {
                tracing::error!(target: "system_events", error_message = %msg, "Password hashing error.");
                ApiError {
                    status_code: 500,
                    message: "Failed to process password securely.".to_string(),
                    errors: None,
                }
            }
            AuthServiceError::InvalidDateFormat(msg) => ApiError {
                status_code: 400,
                message: format!("Invalid date format: {}. Use YYYY-MM-DD.", msg),
                errors: None,
            },
            AuthServiceError::Unexpected(msg) => {
                tracing::error!(target: "system_events", error_message = %msg, "Unexpected error in auth service.");
                ApiError {
                    status_code: 500,
                    message: "An unexpected error occurred.".to_string(),
                    errors: None,
                }
            }
            AuthServiceError::UserNotFound => {
                tracing::warn!(target: "user_events", "Login attempt for non-existent user.");
                ApiError {
                    status_code: 401,
                    message: "Invalid email/username or password.".to_string(),
                    errors: None,
                }
            }
            AuthServiceError::InvalidCredentials => {
                tracing::warn!(target: "user_events", "Login attempt with invalid credentials.");
                ApiError {
                    status_code: 401,
                    message: "Invalid email/username or password.".to_string(),
                    errors: None,
                }
            }
            AuthServiceError::JwtGeneration(e) => {
                tracing::error!(target: "system_events", error = %e, "JWT generation failed during login.");
                ApiError {
                    status_code: 500,
                    message: "Could not process login request due to an internal error.".to_string(),
                    errors: None,
                }
            }
            AuthServiceError::PasswordVerification(msg) => {
                tracing::error!(target: "system_events", error_message = %msg, "Password verification process failed during login.");
                ApiError {
                    status_code: 500,
                    message: "Could not process login request due to a security system error.".to_string(),
                    errors: None,
                }
            }
        }
    }
}

// Added From<JwtError> for ApiError
impl From<JwtError> for ApiError {
    fn from(err: JwtError) -> Self {
        match err {
            JwtError::InvalidToken => ApiError {
                status_code: 401,
                message: "Invalid or malformed token provided.".to_string(),
                errors: None,
            },
            JwtError::TokenExpired => ApiError {
                status_code: 401,
                message: "Token has expired.".to_string(),
                errors: None,
            },
            JwtError::TokenCreation => {
                tracing::error!(target: "system_events", error = %err, "Internal error during token creation for middleware/extractor context.");
                ApiError {
                    status_code: 500,
                    message: "An internal error occurred while processing authentication.".to_string(),
                    errors: None,
                }
            }
            JwtError::MissingEnvVar(var_name) => {
                tracing::error!(target: "system_events", missing_env_var = %var_name, "JWT configuration error detected by middleware.");
                ApiError {
                    status_code: 500,
                    message: "Authentication system configuration error.".to_string(),
                    errors: None,
                }
            }
            JwtError::InvalidEnvVar(var_name) => {
                tracing::error!(target: "system_events", invalid_env_var = %var_name, "JWT configuration error detected by middleware.");
                ApiError {
                    status_code: 500,
                    message: "Authentication system configuration error.".to_string(),
                    errors: None,
                }
            }
            JwtError::Internal(msg) => {
                tracing::error!(target: "system_events", internal_jwt_error = %msg, "Internal JWT library error detected by middleware.");
                ApiError {
                    status_code: 500,
                    message: "An internal error occurred during token validation.".to_string(),
                    errors: None,
                }
            }
        }
    }
}

/// Handles POST requests to `/auth/signup`.
///
/// Validates the incoming `SignupUserDto`, then passes it to the `AuthService`
/// to perform the signup logic.
///
/// # Arguments
/// * `app_state` - `Data<AppState>` injected by Actix.
/// * `auth_service` - `Data<AuthService>` injected by Actix.
/// * `signup_data` - `Json<SignupUserDto>` extracted from the request body.
///
/// # Returns
/// An `impl Responder` which is typically an `HttpResponse`.
#[utoipa::path(
    post,
    path = "/auth/signup",
    request_body = SignupUserDto,
    responses(
        (status = 201, description = "User created successfully", body = UserPublicData),
        (status = 400, description = "Validation error or invalid input", body = ApiError),
        (status = 403, description = "Feature disabled", body = ApiError),
        (status = 409, description = "Email or username already exists", body = ApiError),
        (status = 500, description = "Internal server error", body = ApiError)
    )
)]
pub async fn signup_handler(
    app_state: Data<AppState>,
    auth_service: Data<AuthService>,
    signup_user_dto: Json<SignupUserDto>,
) -> Result<HttpResponse, ApiError> {
    if !app_state.feature_flags.signup_enabled {
        tracing::warn!(target: "user_events", "Signup attempt while feature is disabled.");
        return Err(ApiError {
            status_code: 403,
            message: "Signup feature is currently disabled.".to_string(),
            errors: None,
        });
    }

    if let Err(validation_errors) = signup_user_dto.validate() {
        let mut error_map = HashMap::new();
        for (field, errors) in validation_errors.field_errors() {
            error_map.insert(
                field.to_string(),
                errors
                    .iter()
                    .map(|e| e.message.clone().unwrap_or_default().to_string())
                    .collect(),
            );
        }
        return Err(ApiError {
            status_code: 400,
            message: "Input validation failed.".to_string(),
            errors: Some(error_map),
        });
    }

    let dto = signup_user_dto.into_inner();

    match auth_service.signup_user(dto).await {
        Ok(user_public_data) => Ok(HttpResponse::Created().json(user_public_data)),
        Err(service_error) => {
            if let AuthServiceError::Validation(ref details) = service_error {
                if details.contains_key("email") || details.contains_key("username") {
                    return Err(ApiError {
                        status_code: 409,
                        message: "User with this email or username already exists.".to_string(),
                        errors: Some(details.clone()),
                    });
                }
            }
            Err(service_error.into())
        }
    }
}

/// Handles POST requests to `/auth/login`.
///
/// Validates the incoming `LoginRequest`, then passes it to the `AuthService`
/// to perform the login logic, which includes password verification and JWT generation.
///
/// # Arguments
/// * `app_state` - `Data<AppState>` injected by Actix, used for feature flags.
/// * `auth_service` - `Data<AuthService>` injected by Actix for business logic.
/// * `login_request_dto` - `Json<LoginRequest>` extracted from the request body.
///
/// # Returns
/// An `impl Responder` which is typically an `HttpResponse`.
#[utoipa::path(
    post,
    path = "/auth/login",
    request_body = LoginRequest,
    responses(
        (status = 200, description = "Login successful, JWT returned", body = LoginResponse),
        (status = 400, description = "Validation error or malformed request", body = ApiError),
        (status = 401, description = "Unauthorized - incorrect email/username or password", body = ApiError),
        (status = 403, description = "Feature disabled", body = ApiError),
        (status = 500, description = "Internal server error", body = ApiError)
    )
)]
pub async fn login_handler(
    app_state: Data<AppState>,
    auth_service: Data<AuthService>,
    login_request_dto: Json<LoginRequest>,
) -> Result<HttpResponse, ApiError> {
    if !app_state.feature_flags.login_enabled {
        tracing::warn!(target: "user_events", "Login attempt while feature is disabled.");
        return Err(ApiError {
            status_code: 403,
            message: "Login feature is currently disabled.".to_string(),
            errors: None,
        });
    }

    if let Err(validation_errors) = login_request_dto.validate() {
        let mut error_map = HashMap::new();
        for (field, errors) in validation_errors.field_errors() {
            error_map.insert(
                field.to_string(),
                errors
                    .iter()
                    .map(|e| e.message.clone().unwrap_or_default().to_string())
                    .collect(),
            );
        }
        tracing::warn!(target: "user_events", "Login attempt with invalid input: {:?}", error_map);
        return Err(ApiError {
            status_code: 400,
            message: "Input validation failed.".to_string(),
            errors: Some(error_map),
        });
    }

    let dto = login_request_dto.into_inner();
    tracing::info!(target: "user_events", "Login attempt for user: {}", dto.email_or_username);

    match auth_service.login_user(dto).await {
        Ok(login_response) => {
            tracing::info!(target: "user_events", "User '{}' logged in successfully.", login_response.access_token);
            Ok(HttpResponse::Ok().json(login_response))
        }
        Err(service_error) => {
            Err(service_error.into())
        }
    }
}
