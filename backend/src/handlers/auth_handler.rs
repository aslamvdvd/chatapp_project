use actix_web::{
    web::{Data, Json},
    HttpResponse, ResponseError,
};
use crate::models::user::SignupUserDto;
use crate::services::auth_service::{AuthService, AuthServiceError};
use serde::Serialize;
use validator::Validate;
use std::collections::HashMap;
use utoipa::ToSchema;
use serde_json::json;

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
    status_code: u16,
    message: String,
    #[schema(value_type = Option<HashMap<String, Vec<String>>>)]
    errors: Option<HashMap<String, Vec<String>>>
}

impl std::fmt::Display for ApiError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.message)
    }
}

impl ResponseError for ApiError {
    fn status_code(&self) -> actix_web::http::StatusCode {
        actix_web::http::StatusCode::from_u16(self.status_code).unwrap_or(actix_web::http::StatusCode::INTERNAL_SERVER_ERROR)
    }

    fn error_response(&self) -> HttpResponse {
        let mut response = match self.status_code {
            400 => HttpResponse::BadRequest(),
            409 => HttpResponse::Conflict(), // For existing email/username
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
                tracing::error!(target: "system_events", error = %e, "Database error in auth service during signup attempt.");
                ApiError {
                    status_code: 500,
                    message: "An internal database error occurred.".to_string(),
                    errors: None,
                }
            }
            AuthServiceError::PasswordHashing(msg) => {
                tracing::error!(target: "system_events", error_message = %msg, "Password hashing error during signup attempt.");
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
                tracing::error!(target: "system_events", error_message = %msg, "Unexpected error in auth service during signup attempt.");
                 ApiError {
                    status_code: 500,
                    message: "An unexpected error occurred.".to_string(),
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
        (status = 409, description = "Email or username already exists", body = ApiError),
        (status = 500, description = "Internal server error", body = ApiError)
    )
)]
pub async fn signup_handler(
    auth_service: Data<AuthService>,
    signup_user_dto: Json<SignupUserDto>,
) -> Result<HttpResponse, ApiError> {
    if let Err(validation_errors) = signup_user_dto.validate() {
        let mut error_map = HashMap::new();
        for (field, errors) in validation_errors.field_errors() {
            error_map.insert(
                field.to_string(),
                errors.iter().map(|e| e.message.clone().unwrap_or_default().to_string()).collect(),
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