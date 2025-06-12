# Infrastructure Configuration

This directory contains all the necessary configurations for the infrastructure components that support the GhostTalk platform.

## Component Overview

-   **/docker**:
    -   **Purpose**: Stores the `Dockerfile` for each microservice. This centralizes container definitions and keeps the root of each service clean.
    -   **Example**: `auth-api.Dockerfile`

-   **/postgres**:
    -   **Purpose**: Contains the SQL initialization scripts (`init.sql`) for the PostgreSQL database, defining the initial schema, tables, and roles.

-   **/stun-turn (Coturn)**:
    -   **Purpose**: While the service itself lives in the `services/` directory, its Docker-based infrastructure configuration (like the `coturn.conf` file) could be managed here or directly in its service directory, depending on deployment strategy. The running instance will be managed by the root `docker-compose.yml`.

-   **/prometheus** and **/grafana**:
    -   **Status**: Placeholders
    -   **Purpose**: Will contain the configuration files (`prometheus.yml`, Grafana dashboard definitions) for setting up monitoring, telemetry, and observability for all backend services. 