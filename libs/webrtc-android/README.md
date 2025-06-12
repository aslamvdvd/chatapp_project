# Library: webrtc-android

This Android library will encapsulate all WebRTC-related logic, providing a clean interface for the main application to manage peer connections, media streams, and call state.

## Purpose

-   **Abstraction**: Hides the complexity of the underlying WebRTC implementation (`org.webrtc:google-webrtc`).
-   **Integration**: Provides a simple API for the main Android app to start, answer, and end calls.
-   **State Management**: Manages the `PeerConnection` state machine and media tracks.
-   **Signaling Client**: Contains the client-side logic to communicate with the `call-signaling` service.

## Integration with Main App

The main `android-app` will include this library as a local module in its `settings.gradle.kts` and `build.gradle.kts` files.

**Example `settings.gradle.kts`:**
```kotlin
// In root settings.gradle.kts
include(":apps:android-app")
include(":libs:webrtc-android")
```

**Example `apps/android-app/build.gradle.kts`:**
```kotlin
dependencies {
    implementation(project(":libs:webrtc-android"))
    // ... other dependencies
}
```

## Core Components (Planned)

-   `WebRtcClient`: The main entry point for the library.
-   `PeerConnectionManager`: Handles the creation and management of `PeerConnection` objects.
-   `SignalingClient`: A WebSocket client to interact with the backend signaling service.
-   `CameraManager` & `AudioManager`: Manages local media capture devices. 