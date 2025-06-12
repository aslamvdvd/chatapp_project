# Service: STUN/TURN Server (Coturn)

This service provides STUN (Session Traversal Utilities for NAT) and TURN (Traversal Using Relays around NAT) capabilities, which are essential for establishing WebRTC connections when peers are behind restrictive firewalls or NATs.

We will use the open-source [Coturn](https://github.com/coturn/coturn) server, deployed via Docker.

## Responsibilities

-   **STUN**: Helps clients discover their public IP address and the type of NAT they are behind. This is the first-choice mechanism for establishing a peer-to-peer connection.
-   **TURN**: Acts as a relay server for media streams when a direct peer-to-peer connection cannot be established. This is a fallback that ensures call connectivity but introduces higher latency and server costs.

## Docker Setup

The Coturn server will be configured via `docker-compose.yml` and will use the configuration file located in this directory.

-   **Configuration File**: `docker/coturn.conf`
-   **Docker Image**: `coturn/coturn`

### Ports

The following ports need to be exposed from the container and open on the host machine:

-   `3478/tcp` and `3478/udp`: Standard STUN/TURN port.
-   `49152-65535/udp`: Default port range for TURN relays. This range should be configurable.

### Example `docker-compose.yml` Snippet

```yaml
services:
  stun-turn:
    image: coturn/coturn
    container_name: ghosttalk_coturn
    volumes:
      - ./services/stun-turn/docker/coturn.conf:/etc/coturn/turnserver.conf:ro
    ports:
      - "3478:3478/tcp"
      - "3478:3478/udp"
      - "49152-65535:49152-65535/udp"
    restart: unless-stopped
```

## Security

The TURN server will be secured with a long-term, credential-based authentication mechanism. The `call-signaling` service will be responsible for generating temporary credentials for clients to use when connecting to the TURN server. 