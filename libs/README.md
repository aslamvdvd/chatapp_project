# Shared Libraries

This directory contains shared libraries and modules that can be used by one or more applications or services within the GhostTalk monorepo.

## Library Overview

-   **/webrtc-android**:
    -   **Type**: Android Library
    -   **Purpose**: A dedicated Android module to encapsulate all WebRTC logic, providing a clean API for the main `android-app` to manage calls. It will handle peer connections, media streams, and signaling client logic.

-   **/call-protocol**:
    -   **Type**: Protocol Definition
    -   **Purpose**: Defines the shared Data Transfer Objects (DTOs) and message formats for the call signaling protocol. This ensures the backend and clients speak the same language.

-   **/e2ee-core**:
    -   **Status**: Placeholder
    -   **Purpose**: Will contain the core cryptographic logic for end-to-end encryption, likely implemented in Rust for performance and security.

-   **/signal-protocol-wrapper**:
    -   **Status**: Placeholder
    -   **Purpose**: A JNI/C++ wrapper around a Signal Protocol implementation, making it available to the Android application for E2EE chat.

-   **/media-compression-cpp**:
    -   **Status**: Placeholder
    -   **Purpose**: A native C++ library for performing efficient media compression or processing, potentially used by the `media-processor` service or directly by clients. 