// backend/src/config.rs

use std::env;
use dotenv::dotenv;

#[derive(Debug, Clone)]
pub struct Config {
    pub server_address: String,
    pub port: u16,
}

impl Config {
    pub fn from_env() -> Result<Self, Box<dyn std::error::Error>> {
        dotenv().ok(); // Load .env file if present

        let port = env::var("PORT")
            .unwrap_or_else(|_| "8080".to_string())
            .parse::<u16>()?;
        
        let server_address = env::var("SERVER_ADDRESS").unwrap_or_else(|_| "0.0.0.0".to_string());

        Ok(Config {
            server_address,
            port,
        })
    }
} 