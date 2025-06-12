# Service: Call Signaling

This service is responsible for managing the real-time signaling required to establish, maintain, and terminate WebRTC peer-to-peer connections for audio and video calls.

## Responsibilities

-   **Session Management**: Handles user presence and availability for calls.
-   **WebSocket Broker**: Manages WebSocket connections for all active clients.
-   **SDP Exchange**: Relays Session Description Protocol (SDP) offers and answers between peers.
-   **ICE Candidate Negotiation**: Relays Interactive Connectivity Establishment (ICE) candidates to help peers find the best path to connect.
-   **Call State Machine**: Tracks the state of each call (e.g., `ringing`, `answered`, `in-progress`, `ended`, `rejected`).
-   **Authentication**: Integrates with `auth-api` to authorize client connections.

## Technology Stack (Proposed)

-   **Language**: Rust (using `actix-ws` or `tokio-tungstenite`) or Elixir (using Phoenix Channels).
-   **Protocol**: Secure WebSockets (WSS).
-   **Payload Format**: JSON, defined in `libs/call-protocol`.

## Signaling Flow Example

1.  **Caller A** initiates a call to **Callee B**.
2.  **Caller A** connects to the signaling server via WebSocket.
3.  The server notifies **Callee B** of an incoming call (e.g., via a push notification from `push-service`).
4.  **Callee B** connects to the signaling server.
5.  **Caller A** generates an SDP `offer` and sends it to the server.
6.  The server relays the `offer` to **Callee B**.
7.  **Callee B** generates an SDP `answer` and sends it back to the server.
8.  The server relays the `answer` to **Caller A**.
9.  Both peers exchange ICE candidates through the server to traverse NATs.
10. Once a peer-to-peer connection is established, media flows directly between clients, orchestrated by the `stun-turn` service if needed. 