# Backend Services

This directory contains all the microservices that power the GhostTalk backend. Each subdirectory is a self-contained service with its own logic, and they communicate with each other over the network.

## Service Overview

-   **/auth-api**:
    -   **Language**: Rust
    -   **Responsibilities**: Handles user registration, login, password management, and JWT token issuance. It is the primary gateway for user authentication.

-   **/call-signaling**:
    -   **Language**: Rust or Elixir (TBD)
    -   **Responsibilities**: Manages WebSocket connections for real-time call signaling, including SDP exchange and ICE candidate negotiation, to set up WebRTC peer connections.

-   **/stun-turn**:
    -   **Technology**: Coturn
    -   **Responsibilities**: Provides STUN/TURN server capabilities to assist clients in traversing NATs and firewalls, ensuring reliable call connections.

-   **/profile-api**:
    -   **Status**: Placeholder
    -   **Responsibilities**: Will manage user profiles, statuses, contact lists, and other user-centric data.

-   **/message-service**:
    -   **Status**: Placeholder
    -   **Responsibilities**: Will handle the validation and relaying of end-to-end encrypted chat messages.

-   **(Other Placeholders)**: `media-router`, `media-processor`, `moderation-api`, etc., are placeholders for future capabilities like group calls, media processing, and content moderation. 