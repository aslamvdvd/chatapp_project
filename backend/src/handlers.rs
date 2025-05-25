use actix_web::{get, HttpResponse, Responder};
use serde_json::json;

#[get("/health")]
async fn health_check() -> impl Responder {
    HttpResponse::Ok().json(json!({ "status": "ok" }))
} 