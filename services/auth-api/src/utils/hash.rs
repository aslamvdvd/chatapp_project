use bcrypt::{hash, verify, DEFAULT_COST};
use std::error::Error;

/// Hashes a password using Argon2.
///
/// # Arguments
/// * `password` - The plaintext password to hash.
///
/// # Returns
/// A `Result` containing the hashed password string or an error string.
pub fn hash_password(password: &str) -> Result<String, Box<dyn Error>> {
    Ok(hash(password, DEFAULT_COST)?)
}

/// Verifies a password against a hashed version.
///
/// # Arguments
/// * `password` - The plaintext password.
/// * `hash` - The hashed password string from the database.
///
/// # Returns
/// An `Ok(bool)` indicating whether the password is valid.
/// The caller should specifically check for `false` to handle
/// incorrect password cases, and treat other errors as internal system errors.
pub fn verify_password(password: &str, hash: &str) -> Result<bool, Box<dyn Error>> {
    Ok(verify(password, hash)?)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_password_hash_and_verify() {
        let password = "test_password";
        let hash = hash_password(password).unwrap();
        
        assert!(verify_password(password, &hash).unwrap());
        assert!(!verify_password("wrong_password", &hash).unwrap());
    }
}
