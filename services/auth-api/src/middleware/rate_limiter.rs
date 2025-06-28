use actix_web::{
    dev::{forward_ready, Service, ServiceRequest, ServiceResponse, Transform},
    Error,
};
use futures_util::future::LocalBoxFuture;
use redis::{AsyncCommands, Client};
use std::{
    future::{ready, Ready},
    sync::Arc,
    time::{SystemTime, UNIX_EPOCH},
};
use tracing::{error, warn};

#[derive(Clone)]
pub struct RateLimiter {
    redis_client: Arc<Client>,
    requests_per_minute: u32,
}

impl RateLimiter {
    pub fn new(redis_url: &str, requests_per_minute: u32) -> Result<Self, redis::RedisError> {
        let client = Client::open(redis_url)?;
        Ok(Self {
            redis_client: Arc::new(client),
            requests_per_minute,
        })
    }
}

impl<S, B> Transform<S, ServiceRequest> for RateLimiter
where
    S: Service<ServiceRequest, Response = ServiceResponse<B>, Error = Error>,
    S::Future: 'static,
    B: 'static,
{
    type Response = ServiceResponse<B>;
    type Error = Error;
    type InitError = ();
    type Transform = RateLimiterMiddleware<S>;
    type Future = Ready<Result<Self::Transform, Self::InitError>>;

    fn new_transform(&self, service: S) -> Self::Future {
        ready(Ok(RateLimiterMiddleware {
            service,
            redis_client: self.redis_client.clone(),
            requests_per_minute: self.requests_per_minute,
        }))
    }
}

pub struct RateLimiterMiddleware<S> {
    service: S,
    redis_client: Arc<Client>,
    requests_per_minute: u32,
}

impl<S, B> Service<ServiceRequest> for RateLimiterMiddleware<S>
where
    S: Service<ServiceRequest, Response = ServiceResponse<B>, Error = Error>,
    S::Future: 'static,
    B: 'static,
{
    type Response = ServiceResponse<B>;
    type Error = Error;
    type Future = LocalBoxFuture<'static, Result<Self::Response, Self::Error>>;

    forward_ready!(service);

    fn call(&self, req: ServiceRequest) -> Self::Future {
        let redis_client = self.redis_client.clone();
        let requests_per_minute = self.requests_per_minute;
        let fut = self.service.call(req);

        Box::pin(async move {
            let mut conn = match redis_client.get_async_connection().await {
                Ok(conn) => conn,
                Err(e) => {
                    error!(error = %e, "Failed to connect to Redis");
                    return fut.await;
                }
            };

            let now = SystemTime::now()
                .duration_since(UNIX_EPOCH)
                .unwrap()
                .as_secs();
            let window = now / 60; // Current minute
            let key = format!("rate_limit:{}:{}", "friend_requests", window);

            let count: Option<u32> = conn.get(&key).await.unwrap_or(None);
            let current_count = count.unwrap_or(0);

            if current_count >= requests_per_minute {
                warn!("Rate limit exceeded for friend requests");
                return fut.await;
            }

            // Increment the counter
            let _: () = conn
                .incr(&key, 1)
                .await
                .unwrap_or_else(|e| error!(error = %e, "Failed to increment rate limit counter"));

            // Set expiry to 1 minute if not already set
            let _: () = conn
                .expire(&key, 60)
                .await
                .unwrap_or_else(|e| error!(error = %e, "Failed to set rate limit expiry"));

            fut.await
        })
    }
} 