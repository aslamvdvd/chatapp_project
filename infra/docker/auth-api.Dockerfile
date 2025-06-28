# Stage 1: Builder
FROM rust:slim AS builder

ARG DATABASE_URL
WORKDIR /usr/src/app

# Install build dependencies
RUN apt-get update && apt-get install -y libssl-dev pkg-config && rm -rf /var/lib/apt/lists/*

# Install sqlx-cli to a *known* location
# We'll install it to /usr/local/cargo/bin, which is typically in the PATH of Rust images,
# or we can explicitly copy from there.
# Set CARGO_HOME to a common location
ENV CARGO_HOME=/usr/local/cargo
# Add CARGO_HOME/bin to PATH for this stage
ENV PATH="${CARGO_HOME}/bin:${PATH}"

RUN cargo install sqlx-cli --version 0.7.4 --force --root /usr/local/cargo

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
RUN SQLX_OFFLINE=true cargo build --release

# Stage 2: Runtime
FROM debian:bookworm-slim

WORKDIR /app

# Install runtime dependencies
RUN apt-get update && apt-get install -y libssl3 ca-certificates && rm -rf /var/lib/apt/lists/*

# Copy the migrations directory and binary
COPY --from=builder /usr/src/app/migrations ./migrations
COPY --from=builder /usr/src/app/target/release/auth-api ./auth-api

# **Crucial for migrations service: explicitly copy sqlx-cli from its known install location**
COPY --from=builder /usr/local/cargo/bin/sqlx /usr/local/bin/sqlx


# Ensure the binary is executable
RUN chmod +x ./auth-api
RUN chmod +x /usr/local/bin/sqlx # Make sqlx executable too

# Expose the port
EXPOSE 8080

# Command to run the application
CMD ["./auth-api"]