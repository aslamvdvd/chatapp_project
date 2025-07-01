# Stage 1: Builder
FROM rust:slim AS builder

WORKDIR /usr/src/app

# Install build dependencies
RUN apt-get update && apt-get install -y libssl-dev pkg-config

# Install sqlx-cli, it will be available in this stage for building
RUN cargo install sqlx-cli --version 0.7.4 --force

# Copy dependencies and pre-build them to leverage caching
COPY services/auth-api/Cargo.toml services/auth-api/Cargo.lock ./
RUN mkdir src && echo "fn main() {}" > src/main.rs
RUN cargo build --release
RUN rm -rf src

# Copy the rest of the source code
COPY services/auth-api/src ./src
COPY services/auth-api/migrations ./migrations
COPY services/auth-api/.sqlx ./.sqlx

# Build in offline mode, this is safer and more repeatable for the main app build
ENV SQLX_OFFLINE=true
RUN cargo build --release

# Stage 2: Final application image
FROM debian:bookworm-slim AS final
RUN apt-get update && apt-get install -y openssl ca-certificates && rm -rf /var/lib/apt/lists/*
WORKDIR /usr/src/app
COPY --from=builder /usr/src/app/target/release/auth-api .
COPY ./services/auth-api/migrations ./migrations
ENV RUST_LOG="info"
ENV RUST_BACKTRACE=1
EXPOSE 8080
CMD ["./auth-api"]


# Stage 3: Migrations image
FROM rust:slim AS migrations
WORKDIR /usr/src/app
RUN apt-get update && apt-get install -y libssl-dev pkg-config
RUN cargo install sqlx-cli --version 0.7.4 --force
COPY services/auth-api/migrations ./migrations
COPY services/auth-api/.sqlx ./.sqlx
COPY services/auth-api/Cargo.toml services/auth-api/Cargo.lock ./
COPY services/auth-api/src ./src
RUN cargo build --release
CMD ["/usr/local/cargo/bin/sqlx", "migrate", "run"]