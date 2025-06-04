# Stage 1: Builder
FROM rust:slim as builder

WORKDIR /usr/src/app

# Install build dependencies
# Needed for some crates that link against C libraries (e.g., openssl-sys, some database drivers)
RUN apt-get update && apt-get install -y --no-install-recommends pkg-config libssl-dev clang

# Copy the entire backend source code
COPY . .

# Build the release binary.
# This step benefits from cached Docker layers if dependencies in Cargo.toml haven't changed.
RUN cargo build --release

# Stage 2: Runtime
# Using a slim Debian image for a smaller final image size.
FROM debian:bullseye-slim

WORKDIR /app

# Install runtime dependencies (e.g. SSL certificates)
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates libssl-dev && rm -rf /var/lib/apt/lists/*

# Copy the .env file.
# IMPORTANT: This expects a .env file to be present in the build context's root (/usr/src/app in builder stage)
# For local builds, this .env would be copied from your project's ./backend directory.
# For docker-compose, the .env file at the project root is typically used to substitute variables 
# into docker-compose.yml, and then those are passed as environment variables to the container,
# rather than copying a .env file directly into the image this way usually.
# However, if your app specifically loads a .env file at runtime, this line might be relevant.
# Consider if your app directly reads an .env file or relies purely on environment variables set by Docker Compose.
# If it relies on env vars set by compose, this COPY command for .env might be less critical here,
# as AppConfig.toml or direct env var reading in Rust would be used.
# For now, keeping the COPY of .env.template as .env as per original plan.
COPY --from=builder /usr/src/app/.env.template .env

# Copy only the built binary from the builder stage.
COPY --from=builder /usr/src/app/target/release/chatapp_by_aarchangel_backend .
# Ensure the binary is executable
RUN chmod +x ./chatapp_by_aarchangel_backend

# Expose the port the application will run on.
EXPOSE ${PORT:-8080}

# Command to run the application.
# The backend binary will be executed.
CMD ["./chatapp_by_aarchangel_backend"] 