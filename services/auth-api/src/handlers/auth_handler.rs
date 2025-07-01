use crate::models::auth::LoginRequest;
use crate::models::user::SignupUserDto;
use crate::services::auth_service::{AuthService, AuthServiceError};
use crate::utils::jwt::{AuthenticatedUser, JwtError};
use crate::core::app_state::AppState;
use actix_web::{
    web::{Data, Json},
    HttpResponse,
    ResponseError,
};
use serde::Serialize;
use std::collections::HashMap;
use utoipa::ToSchema;
use validator::Validate;

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
            401 => HttpResponse::Unauthorized(),
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
            AuthServiceError::Database(e) => {
                tracing::error!(target: "system_events", error_message = %e, "Database error occurred.");
                ApiError {
                    message: "An internal database error occurred.".to_string(),
                    status_code: 500,
                    errors: None,
                }
            },
            AuthServiceError::PasswordHashing(e) => {
                tracing::error!(target: "system_events", error_message = %e, "Password hashing error.");
                ApiError {
                    message: "Failed to process password securely.".to_string(),
                    status_code: 500,
                    errors: None,
                }
            },
            AuthServiceError::InvalidCredentials => ApiError {
                message: "Invalid email or password.".to_string(),
                status_code: 401,
                errors: None,
            },
            AuthServiceError::UserExists => ApiError {
                message: "User with this email or username already exists.".to_string(),
                status_code: 409,
                errors: None,
            },
            AuthServiceError::Jwt(e) => {
                tracing::error!(target: "system_events", error_message = %e, "JWT error.");
                ApiError {
                    message: "Failed to generate authentication token.".to_string(),
                    status_code: 500,
                    errors: None,
                }
            },
            AuthServiceError::InvalidDateFormat(e) => ApiError {
                message: format!("Invalid date format: {}. Use YYYY-MM-DD format.", e),
                status_code: 400,
                errors: None,
            },
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

    match auth_service.signup(dto).await {
        Ok(user_public_data) => Ok(HttpResponse::Created().json(user_public_data)),
        Err(service_error) => {
            let api_error = ApiError::from(service_error);
            match api_error.status_code {
                409 => Err(api_error),
                400 => Err(api_error),
                _ => Err(api_error),
            }
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

    match auth_service.login(dto).await {
        Ok(login_response) => {
            tracing::info!(target: "user_events", "User '{}' logged in successfully.", login_response.user.username);
            Ok(HttpResponse::Ok().json(login_response))
        }
        Err(service_error) => {
            let api_error = ApiError::from(service_error);
            tracing::debug!(target: "actix_web::middleware::logger", "Error in response: {:?}", api_error);
            Err(api_error)
        }
    }
}

/// Handles GET requests to `/auth/me`.
///
/// This is a protected endpoint that requires a valid JWT.
/// It uses the `AuthenticatedUser` extractor to get the user's details from the token.
/// It then fetches and returns the user's public profile information.
///
/// # Arguments
/// * `user` - `AuthenticatedUser` extracted from the JWT in the `Authorization` header.
/// * `auth_service` - `Data<AuthService>` injected by Actix for business logic.
///
/// # Returns
/// An `impl Responder` which is typically an `HttpResponse`.
#[utoipa::path(
    get,
    path = "/auth/me",
    responses(
        (status = 200, description = "Authenticated user profile data", body = UserInfoResponse),
        (status = 401, description = "Unauthorized - Invalid or expired token", body = ApiError)
    ),
    security(
        ("bearer_auth" = [])
    )
)]
pub async fn me_handler(
    user: AuthenticatedUser,
    auth_service: Data<AuthService>,
) -> Result<HttpResponse, ApiError> {
    tracing::info!(target: "user_events", "Fetching profile for authenticated user. user_id={}", user.user_id);
    let user_info = auth_service.get_user_by_id(user.user_id).await?;
    Ok(HttpResponse::Ok().json(user_info))
}