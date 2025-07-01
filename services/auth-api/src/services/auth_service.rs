// services/auth-api/src/services/auth_service.rs

use crate::core::rbac::Role;
use crate::models::{
    auth::{LoginRequest, LoginResponse},
    user::{SignupUserDto, UserInfoResponse, User, UserProfile},
};
use crate::utils::jwt::generate_jwt;
use crate::utils::hash::{hash_password, verify_password};
use sqlx::PgPool;
use uuid::Uuid;
use chrono::NaiveDate;

/// Service layer error types.
#[derive(Debug, thiserror::Error)]
pub enum AuthServiceError {
    #[error("Database error: {0}")]
    Database(#[from] sqlx::Error),
    #[error("Password hashing error: {0}")]
    PasswordHashing(String),
    #[error("Invalid credentials")]
    InvalidCredentials,
    #[error("User already exists")]
    UserExists,
    #[error("JWT error: {0}")]
    Jwt(String),
    #[error("Invalid date format: {0}")]
    InvalidDateFormat(String),
}

impl From<argon2::Error> for AuthServiceError {
    fn from(err: argon2::Error) -> Self {
        AuthServiceError::PasswordHashing(err.to_string())
    }
}

impl From<Box<dyn std::error::Error>> for AuthServiceError {
    fn from(err: Box<dyn std::error::Error>) -> Self {
        AuthServiceError::PasswordHashing(err.to_string())
    }
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
    pub fn new(pool: PgPool) -> Self {
        Self { db_pool: pool }
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
    pub async fn signup(&self, signup_data: SignupUserDto) -> Result<UserProfile, AuthServiceError> {
        // Check if user exists
        let existing_user = sqlx::query!(
            r#"
            SELECT id FROM users 
            WHERE email = $1 OR username = $2
            "#,
            signup_data.email,
            signup_data.username
        )
        .fetch_optional(&self.db_pool)
        .await?;

        if existing_user.is_some() {
            return Err(AuthServiceError::UserExists);
        }

        let password_hash = hash_password(&signup_data.password)
            .map_err(|e| AuthServiceError::PasswordHashing(e.to_string()))?;

        let date_of_birth = NaiveDate::parse_from_str(&signup_data.date_of_birth, "%Y-%m-%d")
            .map_err(|e| AuthServiceError::InvalidDateFormat(e.to_string()))?;

        let user = sqlx::query_as!(
            UserProfile,
            r#"
            INSERT INTO users (username, email, password_hash, first_name, last_name, date_of_birth)
            VALUES ($1, $2, $3, $4, $5, $6)
            RETURNING id, username, email, created_at, updated_at
            "#,
            signup_data.username,
            signup_data.email,
            password_hash,
            signup_data.first_name,
            signup_data.last_name,
            date_of_birth
        )
        .fetch_one(&self.db_pool)
        .await?;

        Ok(user)
    }

    pub async fn verify_credentials(&self, email: &str, password: &str) -> Result<UserProfile, AuthServiceError> {
        let user = sqlx::query_as!(
            UserProfile,
            r#"
            SELECT id, username, email, created_at, updated_at
            FROM users
            WHERE email = $1
            "#,
            email
        )
        .fetch_optional(&self.db_pool)
        .await?
        .ok_or(AuthServiceError::InvalidCredentials)?;

        let stored_hash = sqlx::query_scalar!(
            "SELECT password_hash FROM users WHERE id = $1",
            user.id
        )
        .fetch_one(&self.db_pool)
        .await?;

        match verify_password(password, &stored_hash) {
            Ok(true) => Ok(user),
            Ok(false) | Err(_) => Err(AuthServiceError::InvalidCredentials),
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
    pub async fn login(&self, login_data: LoginRequest) -> Result<LoginResponse, AuthServiceError> {
        let mut tx = self.db_pool.begin().await?;

        let user = sqlx::query_as!(
            User, // Use the full User struct from `crate::models::user::User`
            r#"
            SELECT id, email, username, password_hash, first_name, middle_name, last_name, date_of_birth, gender, role as "role!: Role", created_at, updated_at
            FROM users
            WHERE email = $1 OR username = $1
            "#,
            login_data.email_or_username
        )
        .fetch_optional(&mut *tx)
        .await?
        .ok_or(AuthServiceError::InvalidCredentials)?; // If user not found, return invalid credentials


        let stored_hash: String = sqlx::query_scalar!(
            "SELECT password_hash FROM users WHERE id = $1",
            user.id
        )
        .fetch_one(&mut *tx)
        .await?;

        if !verify_password(&login_data.password, &user.password_hash)? {
            return Err(AuthServiceError::InvalidCredentials);
        }

        let token = generate_jwt(user.id, user.role.clone()).map_err(|e| AuthServiceError::Jwt(e.to_string()))?;
        
        let user_info = self.get_user_by_id(user.id).await?;

        tx.commit().await?; // Commit the transaction if everything above was successful
        
        Ok(LoginResponse {
            access_token: token,
            token_type: "Bearer".to_string(),
            user: user_info,
        })
    }

    /// Fetches a user's profile information by their ID.
    ///
    /// # Arguments
    /// * `user_id` - The UUID of the user to fetch.
    ///
    /// # Returns
    /// A `Result` containing the `UserInfoResponse` or an `AuthServiceError`.
    pub async fn get_user_by_id(&self, user_id: Uuid) -> Result<UserInfoResponse, AuthServiceError> {
        let user = sqlx::query_as!(
            UserInfoResponse,
            r#"
            SELECT id, username, email, first_name, middle_name, last_name, created_at, profile_pic
            FROM users
            WHERE id = $1
            "#,
            user_id
        )
        // .fetch_optional(&self.db_pool)
        .fetch_one(&self.db_pool) // Use self.db_pool here as it's a read operation
        .await?;
        // .ok_or(AuthServiceError::InvalidCredentials)?;

        Ok(user)
    }
}
