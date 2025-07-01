.PHONY: all up down logs backend-logs db-logs ps clean prune db-shell migrate-setup migrate-container help sqlx-prepare

# Default target
all: up

## Docker Compose commands
up:
	@echo "Starting up services with Docker Compose (detached mode)..."
	docker compose up --build -d

down:
	@echo "Stopping and removing containers, networks, and volumes..."
	docker compose down -v --remove-orphans

logs:
	@echo "Following logs for all services..."
	docker compose logs -f

backend-logs:
	@echo "Following logs for backend service..."
	docker compose logs -f backend

db-logs:
	@echo "Following logs for db service..."
	docker compose logs -f db

ps:
	@echo "Listing running services..."
	docker compose ps

## Docker System Prune
clean: down
	@echo "Cleaning up Docker system (unused images, networks, etc.)..."
	docker system prune -af

prune: # More aggressive, includes unused volumes defined in compose but not named volumes unless specified
	@echo "WARNING: This will remove all stopped containers, all unused networks, all dangling images, and all build cache."
	@echo "Stopping and removing containers and potentially anonymous volumes..."
	docker compose down -v --remove-orphans
	@echo "Pruning Docker system (including build cache)..."
	docker system prune --all --force --volumes

## Database specific commands
db-recreate:
	@echo "Forcibly stopping and removing the database container and its named volume..."
	docker compose stop db
	docker compose rm -f db
	docker volume rm chatapp_project_pgdata || true
	@echo "Database volume wiped. You can now run 'make up'."

db-shell:
	@echo "Connecting to PostgreSQL shell in the db container (using .env for credentials)..."
	docker compose exec db psql -U $$(grep POSTGRES_USER .env | cut -d '=' -f2) -d $$(grep POSTGRES_DB .env | cut -d '=' -f2)

# --- SQLx Migrations --- 
# Uncomment and adapt if you use sqlx-cli.
# Ensure sqlx-cli is installed where you run these commands (locally or in-container).

# Example: To run migrations using sqlx-cli installed LOCALLY:
# Ensure your .env has DATABASE_URL pointing to localhost:5432 (the exposed port).
# migrate-setup-local:
# 	@echo "(Local sqlx-cli) Creating database (if not exists) and running migrations..."
# 	DATABASE_URL=$$(grep DATABASE_URL .env | sed 's/@db/@localhost/' | cut -d '=' -f2) sqlx database create || true
# 	DATABASE_URL=$$(grep DATABASE_URL .env | sed 's/@db/@localhost/' | cut -d '=' -f2) sqlx migrate run

# Example: To run migrations using sqlx-cli INSIDE THE BACKEND CONTAINER:
# (Assumes sqlx-cli is installed in the backend Docker image)
# migrate-run-container:
# 	@echo "(Container sqlx-cli) Running migrations inside the backend container..."
# 	docker compose exec backend sqlx migrate run --source ./migrations # Adjust --source path if needed

sqlx-prepare:
	@echo "Making sure database is running..."
	docker compose up -d db
	@echo "Waiting for database to be ready..."
	@until docker compose exec db pg_isready -U $$(grep POSTGRES_USER .env | cut -d '=' -f2) -q; do \
		echo "Waiting for db..."; \
		sleep 1; \
	done
	@echo "Database is ready! Preparing sqlx cache..."
	@docker run --rm --network chatapp_project_ghosttalk_net \
		-v ./services/auth-api:/usr/src/app \
		-w /usr/src/app \
		-e DATABASE_URL="postgres://$$(grep POSTGRES_USER .env | cut -d '=' -f2):$$(grep POSTGRES_PASSWORD .env | cut -d '=' -f2)@db:5432/$$(grep POSTGRES_DB .env | cut -d '=' -f2)" \
		rust:slim sh -c "apt-get update > /dev/null && apt-get install -y pkg-config libssl-dev > /dev/null && cargo install sqlx-cli --version 0.7.4 --force > /dev/null && cargo sqlx prepare"
	@echo "SQLx cache updated successfully."

## Help
help:
	@echo "Available commands:"
	@echo "  make all              - Default: Start services (same as make up)"
	@echo "  make up               - Start services (detached mode, builds if necessary)"
	@echo "  make down             - Stop and remove containers, networks, and volumes (including named volumes like pgdata)"
	@echo "  make logs             - Follow logs for all services"
	@echo "  make backend-logs     - Follow logs for the backend service"
	@echo "  make db-logs          - Follow logs for the db service"
	@echo "  make ps               - List running services"
	@echo "  make db-shell         - Connect to PostgreSQL shell in the db container (reads .env for user/db)"
	@echo "  make db-recreate      - Forcefully removes the database and its data volume to allow for a clean start."
	@echo "  make clean            - Stop services and prune unused Docker objects (images, networks, build cache)"
	@echo "  make prune            - Aggressively stop services and prune Docker objects (includes unused volumes)"
	@echo ""
	@echo "SQLx Commands:"
	@echo "  make sqlx-prepare         - Updates the compile-time query cache. Run this after changing SQL in the code."
	@echo ""
	@echo "SQLx Migration Examples (uncomment and adapt in Makefile if using sqlx-cli):"
	@echo "  make migrate-setup-local    - Run migrations using local sqlx-cli against exposed DB port."
	@echo "                              (Requires .env with DATABASE_URL like postgres://user:pass@localhost:5432/dbname)"
	@echo "  make migrate-run-container  - Run migrations using sqlx-cli from within the backend container."
	@echo "                              (Requires sqlx-cli in backend image and migrations folder copied)."
	@echo ""
	@echo "Important: Remember to create and populate your .env file from .env.template." 