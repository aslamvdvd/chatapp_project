# GhostTalk Android Application

This directory contains the native Android client for GhostTalk, built with Kotlin and Jetpack Compose.

## Current State

The application currently supports:
-   User sign-up and login via email.
-   Token-based authentication and session restore.
-   A basic home screen placeholder.

## Future Plans: Call Integration

The foundation has been laid to add audio and video call functionality. The integration will proceed as follows:

1.  **Include WebRTC Library**: The `libs/webrtc-android` module will be included as a project dependency.
2.  **Call ViewModel**: A new `CallViewModel` will be created to manage the state of a call (e.g., `IDLE`, `RINGING`, `IN_CALL`).
3.  **UI Implementation**:
    -   An incoming call notification/screen will be developed.
    -   A dedicated in-call screen will be designed in Jetpack Compose, showing video feeds, call controls (mute, speaker, hang up), and call duration.
4.  **Integration Points**:
    -   The `HomeScreen` or a contact list screen will have UI elements to initiate a call.
    -   A background service or push notification handler will listen for incoming call events from the `call-signaling` service to trigger the incoming call UI.
5.  **Permissions**: The app will be updated to request necessary permissions for camera and microphone access. 