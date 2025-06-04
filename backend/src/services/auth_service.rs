// Placeholder for auth_service.rs

use crate::core::rbac::Role;
use crate::models::user::{SignupUserDto, UserPublicData};
use crate::utils::hash::hash_password;
use chrono::{NaiveDate, Utc};
use sqlx::PgPool;
use std::collections::HashMap;
use uuid::Uuid;

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
}

// Note: For sqlx::query_as to work with (bool,), you might need to derive FromRow for it
// or use a more explicit type that sqlx can map to. Often, a simple struct works best.
// For example: struct Exists { exists: bool; }
// However, for a single boolean, sqlx often handles it with a tuple if the DB returns one column.
// If issues arise, use `
