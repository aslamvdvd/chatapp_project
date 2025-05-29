// Placeholder for hash.rs (Argon2 utils) 

use argon2::{
    password_hash::{
        rand_core::OsRng,
        PasswordHash, PasswordHasher, PasswordVerifier, SaltString
    },
    Argon2
};

/// Hashes a password using Argon2.
///
/// # Arguments
/// * `password` - The password string to hash.
///
/// # Returns
/// A `Result` containing the hashed password string or an error string.
pub fn hash_password(password: &str) -> Result<String, String> {
    // TODO: Consider making Argon2 parameters configurable (e.g., via env vars or config file)
    // for m_cost, t_cost, p_cost. Default values are generally secure.
    let salt = SaltString::generate(&mut OsRng);
    Argon2::default()
        .hash_password(password.as_bytes(), &salt)
        .map(|hash| hash.to_string())
        .map_err(|e| format!("Failed to hash password: {}", e))
}

/// Verifies a password against a stored Argon2 hash.
///
/// # Arguments
/// * `password` - The password string to verify.
/// * `hash_str` - The stored password hash string.
///
/// # Returns
/// `true` if the password matches the hash, `false` otherwise.
/// Returns `false` also if parsing the hash string fails.
pub fn verify_password(password: &str, hash_str: &str) -> bool {
    match PasswordHash::new(hash_str) {
        Ok(parsed_hash) => {
            Argon2::default()
                .verify_password(password.as_bytes(), &parsed_hash)
                .is_ok()
        }
        Err(_) => {
            tracing::warn!(target: "system_events", "Error parsing password hash string during verification. Hash might be malformed or from an incompatible system.");
            false
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_hash_and_verify_password() {
        let password = "securePassword123";
        let hashed_password = hash_password(password).expect("Failed to hash password");

        assert!(verify_password(password, &hashed_password));
        assert!(!verify_password("wrongPassword", &hashed_password));
    }

    #[test]
    fn test_verify_invalid_hash() {
        let password = "securePassword123";
        let invalid_hash = "not_a_valid_argon2_hash";
        assert!(!verify_password(password, invalid_hash));
    }
} 