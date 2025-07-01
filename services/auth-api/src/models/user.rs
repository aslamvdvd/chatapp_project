// Placeholder for user.rs model

use chrono::{DateTime, Datelike, NaiveDate, Utc};
use serde::{Deserialize, Serialize};
use sqlx::FromRow;
use utoipa::ToSchema;
use uuid::Uuid;
use validator::Validate;

/// Represents a user in the system, used for database interactions.
#[derive(Debug, Serialize, Deserialize, FromRow)]
pub struct User {
    pub id: Uuid,
    pub email: String,
    pub username: String,
    pub password_hash: String, // Hashed password
    pub first_name: String,
    pub middle_name: Option<String>,
    pub last_name: String,
    pub date_of_birth: Option<NaiveDate>, // This can be NULL
    pub gender: Option<String>,
    pub role: String,
    pub created_at: DateTime<Utc>,
    pub updated_at: DateTime<Utc>,
}

/// Data transfer object for user signup requests.
#[derive(Debug, Serialize, Deserialize, Validate, ToSchema)]
#[schema(example = json!({
    "email": "user@example.com",
    "username": "exampleuser",
    "first_name": "Example",
    "middle_name": "MiddleName",
    "last_name": "User",
    "date_of_birth": "1990-01-15",
    "gender": "Other",
    "password": "securePassword123",
    "confirm_password": "securePassword123"
}))]
pub struct SignupUserDto {
    #[validate(length(min = 3, max = 30))]
    pub username: String,
    
    #[validate(email)]
    pub email: String,
    
    #[validate(length(min = 8, max = 72))]
    pub password: String,
    
    #[validate(length(min = 1, max = 50))]
    pub first_name: String,
    
    #[validate(length(min = 1, max = 50))]
    pub last_name: String,
    
    #[validate(custom = "validate_dob")]
    pub date_of_birth: String,
}

/// Custom validation function for date_of_birth field.
fn validate_dob(dob: &str) -> Result<(), validator::ValidationError> {
    let date = NaiveDate::parse_from_str(dob, "%Y-%m-%d");
    match date {
        Ok(dob_date) => {
            let today = Utc::now().date_naive();
            let mut age = today.year() - dob_date.year();
            if today.ordinal() < dob_date.ordinal() {
                age -= 1;
            }

            if age < 13 {
                let mut err = validator::ValidationError::new("age_restriction");
                err.message = Some("You must be at least 13 years old to register.".into());
                return Err(err);
            }
            Ok(())
        }
        Err(_) => {
            let mut err = validator::ValidationError::new("invalid_date_format");
            err.message = Some("Date of birth must be in YYYY-MM-DD format.".into());
            Err(err)
        }
    }
}

/// Represents the data returned to the client after a successful signup.
#[derive(Debug, Serialize, Deserialize, ToSchema)]
#[schema(example = json!({
    "id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
    "email": "user@example.com",
    "username": "exampleuser",
    "first_name": "Example",
    "middle_name": "OptionalMiddle",
    "last_name": "User",
    "date_of_birth": "1990-01-15",
    "gender": "Other",
    "role": "user",
    "created_at": "2023-10-27T10:30:00Z"
}))]
pub struct UserPublicData {
    #[schema(format = "uuid", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef")]
    pub id: Uuid,
    #[schema(example = "user@example.com")]
    pub email: String,
    #[schema(example = "exampleuser")]
    pub username: String,
    #[schema(example = "Example")]
    pub first_name: String,
    #[schema(example = "OptionalMiddle")]
    pub middle_name: Option<String>,
    #[schema(example = "User")]
    pub last_name: String,
    #[schema(example = "1990-01-15")]
    pub date_of_birth: Option<String>, // THIS IS THE FIX
    #[schema(example = "Other")]
    pub gender: Option<String>,
    #[schema(example = "user")]
    pub role: String,
    #[schema(format = "date-time", example = "2023-10-27T10:30:00Z")]
    pub created_at: DateTime<Utc>,
}

/// Represents the data returned for an authenticated user's profile.
#[derive(Debug, Serialize, Deserialize, ToSchema)]
#[schema(example = json!({
    "id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
    "email": "user@example.com",
    "username": "exampleuser",
    "first_name": "Example",
    "middle_name": "OptionalMiddle",
    "last_name": "User",
    "created_at": "2023-10-27T10:30:00Z"
}))]
pub struct UserInfoResponse {
    #[schema(format = "uuid")]
    pub id: Uuid,
    pub email: String,
    pub username: String,
    pub first_name: String,
    pub middle_name: Option<String>,
    pub last_name: String,
    #[schema(format = "date-time")]
    pub created_at: DateTime<Utc>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct UserProfile {
    pub id: Uuid,
    pub username: String,
    pub email: String,
    pub created_at: DateTime<Utc>,
    pub updated_at: DateTime<Utc>,
}

#[derive(Debug, Serialize)]
pub struct UserBasicInfo {
    pub id: Uuid,
    pub username: String,
    pub email: String,
    pub full_name: String,
}

impl From<User> for UserPublicData {
    fn from(user: User) -> Self {
        UserPublicData {
            id: user.id,
            email: user.email,
            username: user.username,
            first_name: user.first_name,
            middle_name: user.middle_name,
            last_name: user.last_name,
            date_of_birth: user.date_of_birth.map(|d| d.format("%Y-%m-%d").to_string()), // THIS IS THE FIX
            gender: user.gender,
            role: user.role,
            created_at: user.created_at,
        }
    }
}

impl From<User> for UserInfoResponse {
    fn from(user: User) -> Self {
        UserInfoResponse {
            id: user.id,
            email: user.email,
            username: user.username,
            first_name: user.first_name,
            middle_name: user.middle_name,
            last_name: user.last_name,
            created_at: user.created_at,
        }
    }
}