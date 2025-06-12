# Library: Call Protocol

This library defines the data structures (DTOs) and enumerations for the call signaling protocol. It serves as a shared contract between the backend signaling service and the client applications.

## Purpose

-   **Standardization**: Provides a single source of truth for the signaling message format.
-   **Interoperability**: Ensures that both the Rust backend and the Kotlin client can serialize and deserialize signaling messages consistently.
-   **Clarity**: Clearly defines the structure of offers, answers, ICE candidates, and other call-related events.

## Technology

-   The data structures will be defined in a language-agnostic way, likely using a schema definition language like JSON Schema or directly as Plain Old Data Objects (POCOs/POJOs).
-   For Rust, these will be `structs` with `serde::Serialize` and `serde::Deserialize`.
-   For Kotlin, these will be `data class`es with `kotlinx.serialization.Serializable`.

## Example Data Structures

### `SignalingMessage` (Wrapper)
```json
{
  "type": "offer" | "answer" | "ice_candidate" | "hangup" | "error",
  "payload": { ... }
}
```

### `OfferPayload`
```json
{
  "sdp": "...",
  "from_user_id": "uuid",
  "to_user_id": "uuid",
  "call_id": "uuid"
}
```

### `IceCandidatePayload`
```json
{
  "candidate": "...",
  "sdp_mid": "...",
  "sdp_m_line_index": 0,
  "call_id": "uuid"
}
``` 