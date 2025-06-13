# Channels

This directory contains the core real-time logic for the `chat-gateway`. Each module defines a Phoenix Channel that handles a specific type of communication.

## Topics

Clients join "topics" to send and receive messages. The topic string determines which channel will handle the connection and how messages are routed.

-   **User Channel**: `user:<user_id>`
    -   Used for one-on-one communication and user-specific notifications (like message receipts).
    -   A user can only join their own topic (e.g., user with ID `123` can only join `user:123`).

-   **Group Channel**: `group:<group_id>`
    -   Used for messaging within a "Circle" or group. All members of the group join this topic.
    -   Messages sent to this topic are broadcast to all other members.

-   **Channel Channel**: `channel:<channel_id>`
    -   Used for broadcast-only announcements.
    -   Any user can join to listen, but only authorized admins can send messages.

## Event Payloads

Here are the primary events and their expected payloads.

### `new_msg` (client → server)

-   **Direction**: Client to Server
-   **Description**: Sends a new message to a user or group.
-   **Payload**:
    -   **For UserChannel**: `{ "recipient_id": "uuid", "sender_id": "uuid", "message_id": "uuid", "content": "...", "timestamp": "iso8601" }`
    -   **For GroupChannel**: `{ "group_id": "uuid", "sender_id": "uuid", "message_id": "uuid", "content": "...", "timestamp": "iso8601" }`

### `new_msg` (server → client)

-   **Direction**: Server to Client
-   **Description**: Delivers a new message from another user or group.
-   **Payload**: Same as the incoming `new_msg` payload.

### `typing` (client → server → others)

-   **Direction**: Client to Server, then broadcast to others in the room.
-   **Description**: Indicates that the user has started typing.
-   **Payload**:
    -   **For UserChannel**: `{ "recipient_id": "uuid" }`
    -   **For GroupChannel**: `{ "room_id": "uuid" }`

### `stop_typing` (client → server → others)

-   **Direction**: Client to Server, then broadcast to others in the room.
-   **Description**: Indicates that the user has stopped typing.
-   **Payload**:
    -   **For GroupChannel**: `{ "room_id": "uuid" }`

### `delivered` / `seen` (client → server → sender)

-   **Direction**: Client to Server, then pushed to the original message sender.
-   **Description**: Acknowledges message delivery or that a message has been read.
-   **Payload**: `{ "sender_id": "uuid", "message_id": "uuid" }`

## Notes for Clients

-   When joining a channel, be prepared to handle an `error` response if authorization fails.
-   The server may push an `error` event if you are rate-limited.
-   All timestamps should be in ISO 8601 format. 