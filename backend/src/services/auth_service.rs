// Placeholder for auth_service.rs

use crate::core::rbac::Role;
use crate::models::user::{SignupUserDto, UserPublicData};
use crate::utils::hash::hash_password;
use chrono::{NaiveDate, Utc};
use sqlx::PgPool;
use std::collections::HashMap;
use uuid::Uuid;

// Added for login
use crate::models::auth::{LoginRequest, LoginResponse};
use crate::utils::hash::verify_password;
use crate::utils::jwt::{generate_jwt, JwtError};

/// Service layer error types.
#[derive(Debug, thiserror::Error)]
pub enum AuthServiceError {
    #[error("Validation failed: {0:?}")]
    Validation(HashMap<String, Vec<String>>),
    #[error("Database error: {0}")]
    Database(#[from] sqlx::Error),
    #[error("Password hashing failed: {0}")]
    PasswordHashing(String),
    #[error("Invalid date format for date_of_birth: {0}")]
    InvalidDateFormat(String),
    #[error("An unexpected error occurred: {0}")]
    Unexpected(String),

    // Errors for login
    #[error("User not found.")]
    UserNotFound,
    #[error("Invalid credentials.")]
    InvalidCredentials,
    #[error("JWT generation failed: {0}")]
    JwtGeneration(#[from] JwtError),
    #[error("Password verification failed: {0}")]
    PasswordVerification(String),
}

// Helper struct to fetch user details for authentication
#[derive(sqlx::FromRow)]
struct UserAuthDetails {
    id: Uuid,
    password_hash: String,
    role: Role,
    // email: String, // Potentially useful for context, but not strictly needed for auth logic
    // username: String, // Same as above
}

/// `AuthService` provides methods for authentication related business logic.
#[derive(Clone)]
pub struct AuthService {
    db_pool: PgPool,
}

impl AuthService {
    /// Creates a new `AuthService` instance.
    ///
    /// # Arguments
    /// * `db_pool` - A `PgPool` for database connections.
    pub fn new(db_pool: PgPool) -> Self {
        Self { db_pool }
    }

    /// Handles user signup.
    ///
    /// This involves validating the input, hashing the password, and storing the new user
    /// in the database.
    ///
    /// # Arguments
    /// * `signup_data` - A `SignupUserDto` containing the user's signup information.
    ///
    /// # Returns
    /// A `Result` containing `UserPublicData` on success, or `AuthServiceError` on failure.
    pub async fn signup_user(
        &self,
        signup_data: SignupUserDto,
    ) -> Result<UserPublicData, AuthServiceError> {
        // 1. Validate input (confirm_password is implicitly validated by `must_match`)
        // The validator crate handles this at the DTO deserialization level or handler level.
        // Here, we assume it has been validated by the handler or Actix extractor.

        // 2. Parse date_of_birth
        let dob = NaiveDate::parse_from_str(&signup_data.date_of_birth, "%Y-%m-%d")
            .map_err(|e| AuthServiceError::InvalidDateFormat(e.to_string()))?;

        // 3. Hash password
        let password_hash =
            hash_password(&signup_data.password).map_err(AuthServiceError::PasswordHashing)?;

        // 4. Start a database transaction
        let mut tx = self
            .db_pool
            .begin()
            .await
            .map_err(AuthServiceError::Database)?;

        // 5. Check for existing email or username
        let email_exists: (bool,) =
            sqlx::query_as("SELECT EXISTS(SELECT 1 FROM users WHERE email = $1)")
                .bind(&signup_data.email)
                .fetch_one(&mut *tx)
                .await?;
        if email_exists.0 {
            let mut errors = HashMap::new();
            errors.insert(
                "email".to_string(),
                vec!["Email already exists.".to_string()],
            );
            return Err(AuthServiceError::Validation(errors));
        }

        let username_exists: (bool,) =
            sqlx::query_as("SELECT EXISTS(SELECT 1 FROM users WHERE username = $1)")
                .bind(&signup_data.username)
                .fetch_one(&mut *tx)
                .await?;
        if username_exists.0 {
            let mut errors = HashMap::new();
            errors.insert(
                "username".to_string(),
                vec!["Username already exists.".to_string()],
            );
            return Err(AuthServiceError::Validation(errors));
        }

        // 6. Create new user
        let new_user_id = Uuid::new_v4();
        let now = Utc::now();
        let default_role = Role::default();

        let result = sqlx::query(
            "INSERT INTO users (id, email, username, password_hash, first_name, middle_name, last_name, date_of_birth, gender, role, created_at, updated_at) \n             VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12)"
        )
        .bind(new_user_id)
        .bind(&signup_data.email)
        .bind(&signup_data.username)
        .bind(&password_hash)
        .bind(&signup_data.first_name)
        .bind(signup_data.middle_name.as_ref())
        .bind(&signup_data.last_name)
        .bind(dob)
        .bind(signup_data.gender.as_ref())
        .bind(default_role)
        .bind(now)
        .bind(now)
        .execute(&mut *tx)
        .await;

        match result {
            Ok(_) => {
                tx.commit().await.map_err(AuthServiceError::Database)?;

                // TODO: Send verification email

                Ok(UserPublicData {
                    id: new_user_id,
                    email: signup_data.email,
                    username: signup_data.username,
                    first_name: signup_data.first_name,
                    middle_name: signup_data.middle_name,
                    last_name: signup_data.last_name,
                    date_of_birth: signup_data.date_of_birth,
                    gender: signup_data.gender,
                    role: default_role,
                    created_at: now,
                })
            }
            Err(e) => {
                let _ = tx.rollback().await;
                Err(AuthServiceError::Database(e))
            }
        }
    }

    /// Handles user login.
    ///
    /// This involves finding the user by email or username, verifying the password,
    /// and generating a JWT if credentials are correct.
    ///
    /// # Arguments
    /// * `login_data` - A `LoginRequest` containing the user's login credentials.
    ///
    /// # Returns
    /// A `Result` containing `LoginResponse` on success, or `AuthServiceError` on failure.
    pub async fn login_user(
        &self,
        login_data: LoginRequest,
    ) -> Result<LoginResponse, AuthServiceError> {
        // 1. Find user by email or username (case-insensitive for username/email)
        // Using LOWER() for case-insensitive comparison on email and username.
        // Ensure that if you have separate indexes on email/username, they are created with LOWER() too,
        // or that your database collation handles this efficiently.
        let user_details = sqlx::query_as::<_, UserAuthDetails>(
            "SELECT id, password_hash, role FROM users WHERE LOWER(email) = LOWER($1) OR LOWER(username) = LOWER($1)"
        )
        .bind(&login_data.email_or_username)
        .fetch_optional(&self.db_pool)
        .await?;

        let user = match user_details {
            Some(u) => u,
            None => return Err(AuthServiceError::UserNotFound),
        };

        // 2. Verify password
        let is_password_valid = verify_password(&login_data.password, &user.password_hash);

        if !is_password_valid {
            // This will be triggered if the password is wrong OR if the hash_str was malformed
            // (as verify_password returns false in that case too).
            return Err(AuthServiceError::InvalidCredentials);
        }

        // 3. Generate JWT
        let token = generate_jwt(user.id, user.role.clone())?; // clone role if needed by generate_jwt

        // 4. Return LoginResponse
        Ok(LoginResponse {
            access_token: token,
            token_type: "Bearer".to_string(),
        })
    }
}

// Note: For sqlx::query_as to work with (bool,), you might need to derive FromRow for it
// or use a more explicit type that sqlx can map to. Often, a simple struct works best.
// For example: struct Exists { exists: bool; }
// However, for a single boolean, sqlx often handles it with a tuple if the DB returns one column.
// If issues arise, use `
