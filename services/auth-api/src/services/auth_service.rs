use crate::core::rbac::Role;
use crate::models::{
    auth::{LoginRequest, LoginResponse},
    user::{SignupUserDto, User, UserInfoResponse, UserLoginInfo, UserPublicData},
};
use crate::utils::jwt::generate_jwt;
use crate::utils::hash::{hash_password, verify_password};
use sqlx::PgPool;
use uuid::Uuid;
use chrono::NaiveDate;
use std::str::FromStr;

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
    pub async fn signup(&self, signup_data: SignupUserDto) -> Result<UserPublicData, AuthServiceError> {
        let mut tx = self.db_pool.begin().await?;

        // Check if user exists
        let existing_user = sqlx::query!(
            r#"
            SELECT id FROM users 
            WHERE email = $1 OR username = $2
            "#,
            signup_data.email,
            signup_data.username
        )
        .fetch_optional(&mut *tx)
        .await?;

        if existing_user.is_some() {
            return Err(AuthServiceError::UserExists);
        }

        let password_hash = hash_password(&signup_data.password)
            .map_err(|e| AuthServiceError::PasswordHashing(e.to_string()))?;

        let date_of_birth = NaiveDate::parse_from_str(&signup_data.date_of_birth, "%Y-%m-%d")
            .map_err(|e| AuthServiceError::InvalidDateFormat(e.to_string()))?;

        let record = sqlx::query!(
            r#"
            INSERT INTO users (username, email, password_hash, first_name, last_name, middle_name, date_of_birth, gender)
            VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
            RETURNING id, username, email, password_hash, first_name, middle_name, last_name, date_of_birth, gender::text as gender, role::text as role, created_at, updated_at
            "#,
            signup_data.username,
            signup_data.email,
            password_hash,
            signup_data.first_name,
            signup_data.last_name,
            signup_data.middle_name,
            date_of_birth,
            signup_data.gender
        )
        .fetch_one(&mut *tx)
        .await?;
        
        tx.commit().await?;

        let user = User {
            id: record.id,
            email: record.email,
            username: record.username,
            password_hash: record.password_hash,
            first_name: record.first_name,
            middle_name: record.middle_name,
            last_name: record.last_name,
            date_of_birth: Some(record.date_of_birth),
            gender: record.gender,
            role: record.role.unwrap(),
            created_at: record.created_at,
            updated_at: record.updated_at,
        };

        Ok(user.into())
    }

    pub async fn verify_credentials(&self, email: &str, password: &str) -> Result<User, AuthServiceError> {
        let record = sqlx::query!(
            r#"
            SELECT id, username, email, password_hash, first_name, middle_name, last_name, date_of_birth, gender::text as gender, role::text as role, created_at, updated_at
            FROM users
            WHERE email = $1
            "#,
            email
        )
        .fetch_optional(&self.db_pool)
        .await?
        .ok_or(AuthServiceError::InvalidCredentials)?;

        let user = User {
            id: record.id,
            email: record.email,
            username: record.username,
            password_hash: record.password_hash,
            first_name: record.first_name,
            middle_name: record.middle_name,
            last_name: record.last_name,
            date_of_birth: Some(record.date_of_birth),
            gender: record.gender,
            role: record.role.unwrap(),
            created_at: record.created_at,
            updated_at: record.updated_at,
        };

        match verify_password(password, &user.password_hash) {
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

        let record = sqlx::query!(
            r#"
            SELECT id, username, email, password_hash, first_name, middle_name, last_name, date_of_birth, gender::text as gender, role::text as role, created_at, updated_at
            FROM users 
            WHERE email = $1 OR username = $1
            "#,
            login_data.email_or_username
        )
        .fetch_optional(&mut *tx)
        .await?
        .ok_or(AuthServiceError::InvalidCredentials)?;

        let user = User {
            id: record.id,
            email: record.email,
            username: record.username,
            password_hash: record.password_hash,
            first_name: record.first_name,
            middle_name: record.middle_name,
            last_name: record.last_name,
            date_of_birth: Some(record.date_of_birth),
            gender: record.gender,
            role: record.role.unwrap(),
            created_at: record.created_at,
            updated_at: record.updated_at,
        };

        if !verify_password(&login_data.password, &user.password_hash)? {
            return Err(AuthServiceError::InvalidCredentials);
        }

        let role = Role::from_str(&user.role).unwrap_or(Role::User);
        let token = generate_jwt(user.id, role).map_err(|e| AuthServiceError::Jwt(e.to_string()))?;
        
        tx.commit().await?;

        Ok(LoginResponse {
            token,
            user: user.into(),
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
            SELECT id, username, email, first_name, middle_name, last_name, created_at
            FROM users
            WHERE id = $1
            "#,
            user_id
        )
        .fetch_optional(&self.db_pool)
        .await?
        .ok_or(AuthServiceError::InvalidCredentials)?;

        Ok(user)
    }
} 