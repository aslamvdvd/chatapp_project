use rand::RngCore;
use base64::{engine::general_purpose, Engine as _};

fn generate_jwt_secret() -> String {
    // A good length for a JWT secret key for HS256 (HMAC-SHA256) is at least 32 bytes (256 bits).
    // For stronger algorithms like HS512, you'd want 64 bytes (512 bits).
    // Let's go with 32 bytes for now.
    let mut key_bytes = [0u8; 32];
    
    // Fill the byte array with cryptographically secure random bytes
    rand::thread_rng().fill_bytes(&mut key_bytes);
    
    // Encode the bytes to Base64 to make it a safe string for environment variables
    general_purpose::STANDARD.encode(&key_bytes)
}

fn main() {
    let secret_key = generate_jwt_secret();
    println!("Generated JWT Secret: {}", secret_key);
    println!("Add this to your .env file: JWT_SECRET={}", secret_key);
}