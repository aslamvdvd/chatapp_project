use crate::handlers::auth_handler::ApiError;
use serde::{Deserialize, Serialize}; // Assuming ApiError is made public or moved
use std::str::FromStr;
use std::fmt;

/// Defines user roles within the application.
///
/// Note: Guest access is not supported in this application.
#[derive(
    Debug, Clone, Copy, PartialEq, Eq, Serialize, Deserialize, sqlx::Type, utoipa::ToSchema,
)]
#[sqlx(type_name = "user_role", rename_all = "lowercase")] // For PostgreSQL enum mapping
#[schema(example = "user")]
pub enum Role {
    User,
    Admin,
}

impl Default for Role {
    fn default() -> Self {
        Role::User
    }
}

impl fmt::Display for Role {
    fn fmt(&self, f: &mut fmt::Formatter) -> fmt::Result {
        write!(f, "{}", self.as_str())
    }
}

impl FromStr for Role {
    type Err = String;

    fn from_str(input: &str) -> Result<Role, Self::Err> {
        match input.to_lowercase().as_str() {
            "user" => Ok(Role::User),
            "admin" => Ok(Role::Admin),
            _ => Err(format!("Invalid role: {}", input)),
        }
    }
}

impl Role {
    pub fn as_str(&self) -> &'static str {
        match self {
            Role::User => "user",
            Role::Admin => "admin",
        }
    }
}

/// Checks if a given role is present in a slice of allowed roles.
///
/// This is a basic helper for RBAC checks. In a real application, this might be part of
/// an Actix middleware or a more sophisticated authorization service.
///
/// # Arguments
/// * `user_role` - The role of the current user.
/// * `allowed_roles` - A slice of `Role`s that are permitted for the action.
///
/// # Returns
/// `Ok(())` if the `user_role` is in `allowed_roles`, otherwise an `ApiError` (Forbidden).
pub fn require_role(user_role: Role, allowed_roles: &[Role]) -> Result<(), ApiError> {
    if !allowed_roles.contains(&user_role) {
        tracing::warn!(
            target: "user_events",
            current_role = ?user_role,
            required_roles = ?allowed_roles,
            "Access denied due to insufficient role."
        );
        Err(ApiError {
            status_code: 403, // Forbidden
            message: "Access Denied: You do not have the required role for this action."
                .to_string(),
            errors: None,
        })
    } else {
        Ok(())
    }
}

// Example of how to represent user data that might come from a JWT or session
// For now, this is a placeholder.
#[derive(Debug, Clone)]
pub struct AuthenticatedUser {
    pub id: uuid::Uuid,
    pub email: String,
    pub username: String,
    pub role: Role,
}

// TODO: Implement JWT parsing or session management to populate AuthenticatedUser.
// TODO: Create an Actix Extractor for AuthenticatedUser to easily get it in handlers.

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_require_role_user_allowed() {
        assert!(require_role(Role::User, &[Role::User, Role::Admin]).is_ok());
    }

    #[test]
    fn test_require_role_admin_allowed() {
        assert!(require_role(Role::Admin, &[Role::Admin]).is_ok());
    }

    #[test]
    fn test_require_role_user_not_allowed() {
        let result = require_role(Role::User, &[Role::Admin]);
        assert!(result.is_err());
        if let Err(api_error) = result {
            assert_eq!(api_error.status_code, 403);
        }
    }

    #[test]
    fn test_require_role_empty_allowed_roles() {
        let result = require_role(Role::User, &[]);
        assert!(result.is_err());
    }
}
