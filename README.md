# GhostTalk - Privacy-Focused Microservice Chat Application

This repository contains the source code for GhostTalk, a privacy-focused chat application being built with a microservice architecture.

## Project Vision

GhostTalk aims to be a secure, private, and modern messaging platform. By leveraging a microservice backend and native clients, it is designed for scalability, resilience, and maintainability.

## Project Structure

The project is a monorepo organized by domain (`apps`, `services`, `libs`, `infra`) to support a growing number of services and shared modules.

```
ghosttalk/
├── apps/
│   ├── android-app/                  # Native Android client (Kotlin/Compose)
│   └── admin-dashboard/              # Placeholder for web-based admin panel
├── services/
│   ├── auth-api/                     # Handles user authentication and registration
│   └── ... (placeholders for chat, profiles, etc.)
├── libs/
│   ├── ... (placeholders for shared Rust/C++/JNI libraries)
├── infra/
│   ├── docker/                       # Service Dockerfiles
│   ├── postgres/                     # Database schemas and initialization
│   ├── docker-compose.yml            # Main composition for development
│   └── ... (placeholders for Prometheus, Grafana, etc.)
├── scripts/
│   ├── init_env.sh                   # Environment setup script
│   └── seed_db.sh                    # Database seeder (placeholder)
├── .env.example                      # Example environment variables
├── .gitignore
├── README.md                         # This file
└── LICENSE
```

## Getting Started

### Prerequisites

-   **Docker & Docker Compose:** For running the backend services.
-   **Rust & Cargo:** For building and running the `auth-api` service.
-   **Android Studio & Gradle:** For building and running the Android application.

### 1. Initial Environment Setup

First, prepare your local environment by creating a `.env` file from the provided template.

```bash
# Make sure the script is executable
chmod +x scripts/init_env.sh

# Run the script
./scripts/init_env.sh
```

This will create a `.env` file at the root of the project. **Review and update the variables** in this file, especially the database credentials.

### 2. Running the Backend

The entire backend stack can be started using Docker Compose.

```bash
docker-compose up --build -d
```

This will:
1.  Build the `auth-api` service from its Dockerfile.
2.  Start a PostgreSQL database container.
3.  Run the database initialization script from `infra/postgres/init.sql`.
4.  Start the `auth-api` service, which will connect to the database.

The `auth-api` will be available at `http://localhost:8080` (or the port you configure in `.env`).

### 3. Running the Android App

1.  Open the Android project located at `apps/android-app/` in Android Studio.
2.  Let Gradle sync the project dependencies.
3.  **Important:** Ensure the `API_URL` in `apps/android-app/app/build.gradle.kts` matches the address of your `auth-api` service as seen from your Android device/emulator (e.g., `http://192.168.1.100:8080`, not `localhost`).
4.  Run the app on a connected device or emulator.

## Contributing

Please see `CONTRIBUTING.md` and `CODE_OF_CONDUCT.md` for details on how to contribute to this project.

## License

This project is licensed under the terms of the `LICENSE` file. 