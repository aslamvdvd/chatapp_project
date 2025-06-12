// Placeholder for hash.rs (Argon2 utils)

use argon2::{
    password_hash::{rand_core, Error, PasswordHash, PasswordHasher, PasswordVerifier, SaltString},
    Argon2,
};

/// Hashes a password using Argon2.
///
/// # Arguments
/// * `password` - The plaintext password to hash.
///
/// # Returns
/// A `Result` containing the hashed password string or an error string.
pub fn hash_password(password: &str) -> Result<String, String> {
    // TODO: Consider making Argon2 parameters configurable (e.g., via env vars or config file)
    // for m_cost, t_cost, p_cost. Default values are generally secure.
    let salt = SaltString::generate(&mut rand_core::OsRng);
    let argon2 = Argon2::default();
    let password_hash = argon2
        .hash_password(password.as_bytes(), &salt)
        .map_err(|e| e.to_string())?
        .to_string();
    Ok(password_hash)
}

/// Verifies a password against a hashed version.
///
/// # Arguments
/// * `password` - The plaintext password.
/// * `hash_str` - The hashed password string from the database.
///
/// # Returns
/// An `Ok(())` if the password is valid. Otherwise, returns a `password_hash::Error`.
/// The caller should specifically check for `password_hash::Error::Password` to handle
/// incorrect password cases, and treat other errors as internal system errors.
pub fn verify_password(password: &str, hash_str: &str) -> Result<(), Error> {
    let parsed_hash = PasswordHash::new(hash_str)?;

    Argon2::default().verify_password(password.as_bytes(), &parsed_hash)
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_hash_and_verify_password() {
        let password = "mySecurePassword123";
        let hash = hash_password(password).unwrap();
        assert!(verify_password(password, &hash).is_ok());
    }

    #[test]
    fn test_verify_invalid_password() {
        let password = "mySecurePassword123";
        let wrong_password = "wrongPassword";
        let hash = hash_password(password).unwrap();
        let result = verify_password(wrong_password, &hash);
        assert!(matches!(result, Err(Error::Password)));
    }

    #[test]
    fn test_verify_invalid_hash() {
        let password = "mySecurePassword123";
        let invalid_hash = "this-is-not-a-valid-hash";
        let result = verify_password(password, invalid_hash);
        assert!(result.is_err());
        assert!(!matches!(result, Err(Error::Password))); // Should be a parsing error, not a password mismatch
    }
}
