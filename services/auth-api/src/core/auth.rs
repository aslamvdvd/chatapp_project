use std::future::{ready, Ready};
use actix_web::{FromRequest, HttpRequest, dev::Payload};
use actix_web::error::ErrorUnauthorized;
use jsonwebtoken::{decode, Validation, DecodingKey};
use serde::{Serialize, Deserialize};
use crate::core::rbac::{AuthenticatedUser, Role};
use uuid::Uuid;

#[derive(Debug, Serialize, Deserialize)]
pub struct Claims {
    pub sub: String, // Subject (user id)
    pub role: String,
    pub exp: usize,    // Expiration time
    pub email: String,
    pub username: String,
}

impl FromRequest for AuthenticatedUser {
    type Error = actix_web::Error;
    type Future = Ready<Result<Self, Self::Error>>;

    fn from_request(req: &HttpRequest, _: &mut Payload) -> Self::Future {
        let auth_header = req.headers().get("Authorization");

        if auth_header.is_none() {
            return ready(Err(ErrorUnauthorized("No token provided")));
        }

        let auth_str = auth_header.unwrap().to_str().unwrap_or("");
        if !auth_str.starts_with("Bearer ") {
            return ready(Err(ErrorUnauthorized("Invalid token format")));
        }

        let token = &auth_str[7..];

        let jwt_secret = std::env::var("JWT_SECRET").expect("JWT_SECRET must be set");

        let decoding_key = DecodingKey::from_secret(jwt_secret.as_ref());
        let validation = Validation::default();

        match decode::<Claims>(&token, &decoding_key, &validation) {
            Ok(token_data) => {
                let claims = token_data.claims;
                let user_role = match claims.role.as_str() {
                    "admin" => Role::Admin,
                    _ => Role::User,
                };
                
                let user_id = match Uuid::parse_str(&claims.sub) {
                    Ok(id) => id,
                    Err(_) => return ready(Err(ErrorUnauthorized("Invalid user ID in token"))),
                };

                ready(Ok(AuthenticatedUser {
                    id: user_id,
                    email: claims.email,
                    username: claims.username,
                    role: user_role,
                }))
            },
            Err(_) => ready(Err(ErrorUnauthorized("Invalid token"))),
        }
    }
} 