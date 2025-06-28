# Stage 1: Builder
FROM rust:slim as builder

ARG DATABASE_URL
WORKDIR /usr/src/app

# Install build dependencies
RUN apt-get update && apt-get install -y libssl-dev pkg-config && rm -rf /var/lib/apt/lists/*

# Install sqlx-cli
RUN cargo install sqlx-cli

# Copy the Cargo.toml and Cargo.lock files first to cache dependencies
COPY services/auth-api/Cargo.toml services/auth-api/Cargo.lock ./

# Create a dummy main.rs to build dependencies
RUN mkdir src && echo "fn main() {}" > src/main.rs

# Build dependencies
RUN cargo build --release

# Remove the dummy source code
RUN rm -rf src

# Copy the actual source code
COPY services/auth-api/src ./src
COPY services/auth-api/migrations ./migrations
COPY services/auth-api/.sqlx ./.sqlx

# Clean, update, and build
RUN cargo clean
RUN cargo update
RUN cargo build --release

# Stage 2: Runtime
FROM debian:bookworm-slim

WORKDIR /app

# Install runtime dependencies
RUN apt-get update && apt-get install -y libssl3 ca-certificates && rm -rf /var/lib/apt/lists/*

# Copy the migrations directory and binary
COPY --from=builder /usr/src/app/migrations ./migrations
COPY --from=builder /usr/src/app/target/release/auth-api ./auth-api

# Ensure the binary is executable
RUN chmod +x ./auth-api

# Expose the port
EXPOSE ${PORT:-8080}

# Command to run the application
CMD ["./auth-api"] 