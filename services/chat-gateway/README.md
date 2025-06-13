# Chat Gateway

The `chat-gateway` is a standalone Elixir/Phoenix microservice responsible for handling all real-time bidirectional communication for the GhostTalk application. It uses WebSockets to provide low-latency messaging for one-on-one chats, group chats (Circles), and broadcast channels.

## Core Responsibilities

-   **WebSocket Connection Management**: Manages the lifecycle of WebSocket connections from clients.
-   **Authentication**: Validates JWTs for every incoming connection to ensure security.
-   **Message Routing**: Routes messages between users, groups, and channels based on topics.
-   **Real-time Events**: Handles events like `new_msg`, `typing`, `delivered`, and `seen`.
-   **Rate Limiting**: Provides basic in-memory rate limiting to prevent abuse.

## Getting Started

1.  **Install Dependencies**:
    ```bash
    mix deps.get
    ```

2.  **Configure Environment**:
    -   Ensure you have a `JWT_SECRET` configured. This is set in `config/config.exs`. For production, this should be an environment variable.

3.  **Run the server**:
    ```bash
    iex -S mix phx.server
    ```
    The server will typically start on `localhost:4000`.

## Connecting to the Socket

Clients should connect to the WebSocket endpoint at `ws://<host>:<port>/socket`.

### Authentication

To authenticate, the client must include a `token` parameter in the connection URL's query string.

**Example URL**:
`ws://localhost:4000/socket/websocket?token=YOUR_JWT_HERE`

The JWT must be valid and signed with the configured `JWT_SECRET`. The payload of the JWT must include a `sub` (subject) claim containing the user's unique ID.

## Topics and Events

See the README in `lib/chat_gateway_web/channels/` for detailed information on topics and event payloads.

## Learn more

  * Official website: https://www.phoenixframework.org/
  * Guides: https://hexdocs.pm/phoenix/overview.html
  * Docs: https://hexdocs.pm/phoenix
  * Forum: https://elixirforum.com/c/phoenix-forum
  * Source: https://github.com/phoenixframework/phoenix
