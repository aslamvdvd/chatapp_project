# Authentication

This module handles the authentication of clients connecting to the `chat-gateway`.

## `JWTAuth` Module

-   **Responsibility**: The `JWTAuth` module is responsible for verifying the JSON Web Token (JWT) provided by a client during the socket connection handshake.
-   **Mechanism**: It uses the `Joken` library to decode and validate the token. The signing secret is fetched from the application's configuration, which allows for different secrets in development and production environments.

## JWT Requirements for Clients

To successfully authenticate, the client must provide a JWT that meets the following criteria:

1.  **Algorithm**: The token must be signed with the `HS256` (HMAC using SHA-256) algorithm.
2.  **Secret**: It must be signed with the same secret key that the `chat-gateway` is configured with.
3.  **Claims**: The token's payload (claims) **must** contain a `sub` (subject) claim. The value of the `sub` claim should be the unique identifier (UUID or ID) of the user.

### Example JWT Payload

```json
{
  "aud": "user",
  "exp": 1672531199,
  "iat": 1672527600,
  "iss": "ghosttalk-auth-api",
  "jti": "some-unique-id",
  "nbf": 1672527599,
  "sub": "user-uuid-12345",
  "is_admin": false
}
```

The `sub` claim is critical, as it is used to identify the user for the lifetime of the socket connection. The optional `is_admin` claim could be used to grant publishing rights to broadcast-only channels. 