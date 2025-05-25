# GhostTalk - Privacy-Focused Chat Application

This repository contains the source code for GhostTalk, a privacy-focused chat application.
It is structured as a monorepo with a native Android frontend and a Rust-based backend.

## Project Structure

```
GhostTalk/
├── android-app/            # Native Android frontend (Kotlin, Jetpack Compose)
├── backend/                # Rust + Actix Web API
├── .gitignore
├── CODE_OF_CONDUCT.md
├── CONTRIBUTING.md
├── LICENSE
├── README.md               # This file (root README)
└── settings.gradle.kts     # Root Gradle settings (for Android project)
```

## Modules

### 1. `android-app/`

This directory contains the native Android application built with Kotlin and Jetpack Compose.

**Build & Run (Android):**

1.  Navigate to the `android-app/` directory:
    ```bash
    cd android-app
    ```
2.  **Ensure you have Android Studio installed** (latest stable version recommended) or at least the Android SDK and a compatible JDK (e.g., JDK 17).
3.  **Build the project using Gradle Wrapper:**
    ```bash
    ./gradlew build
    ```
4.  **Install on a connected device or emulator:**
    ```bash
    ./gradlew installDebug
    ```
    Alternatively, open the `android-app/` project in Android Studio and run it directly from the IDE.

For more details on the Android application, see `android-app/README.md`.

### 2. `backend/`

This directory contains the backend API server built with Rust and the Actix Web framework.

**Setup & Run (Backend):**

1.  **Ensure you have Rust installed.** You can install it from [rust-lang.org](https://www.rust-lang.org/).
2.  Navigate to the `backend/` directory:
    ```bash
    cd backend
    ```
3.  **(Optional) Create a `.env` file** from `.env.template` and customize if needed:
    ```bash
    cp .env.template .env
    # Modify .env with your desired PORT, e.g., PORT=8080
    ```
4.  **Build the backend (debug mode):**
    ```bash
    cargo build
    ```
5.  **Run the backend server (debug mode):**
    ```bash
    cargo run
    ```
    The server will typically start on `http://0.0.0.0:8080` (or the port specified in `.env`).
    The health check endpoint will be available at `GET /api/health`.

6.  **Build for release:**
    ```bash
    cargo build --release
    ```

## Contributing

Please see `CONTRIBUTING.md` and `CODE_OF_CONDUCT.md` for details on how to contribute to this project.

## License

This project is licensed under the terms of the `LICENSE` file. 