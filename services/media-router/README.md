# Service: Media Router (SFU/MCU) - Optional

This service is a placeholder for a future media router, such as a Selective Forwarding Unit (SFU) or Multipoint Control Unit (MCU). While not required for 1-on-1 calls, a media router is essential for group calls, recording, and advanced media processing.

## Potential Responsibilities

-   **Group Calls**: Efficiently route media streams for calls with more than two participants, reducing client-side bandwidth requirements.
-   **Recording**: Centrally record audio and video streams for compliance, archival, or on-demand playback.
-   **Transcoding**: (MCU-specific) Mix multiple streams into a single composite stream.
-   **Media Analytics**: Gather statistics on call quality, packet loss, and jitter for monitoring and diagnostics.
-   **Gatewaying**: Bridge WebRTC calls with other protocols like SIP or RTMP.

## Potential Technology Choices

-   [Jitsi Video Bridge](https://github.com/jitsi/jitsi-videobridge) (Java)
-   [Janus WebRTC Server](https://github.com/meetecho/janus-gateway) (C)
-   [mediasoup](https://mediasoup.org/) (Node.js/C++)
-   [LiveKit](https://livekit.io/) (Go)

## Integration

When implemented, the `call-signaling` service would instruct clients to connect to this media router instead of directly to each other for applicable calls. The media router would have its own signaling and control plane. 