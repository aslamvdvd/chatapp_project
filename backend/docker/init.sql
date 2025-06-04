-- Create extensions if they don't exist
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Example: Create a specific role if needed (adjust permissions as necessary)
-- DO $$
-- BEGIN
--   IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'app_user') THEN
-- CREATE ROLE app_user LOGIN PASSWORD 'secure_password';
-- END IF;
-- END
-- $$;

-- Example: Grant privileges (if you created a specific app_user)
-- GRANT ALL PRIVILEGES ON DATABASE ${POSTGRES_DB} TO app_user; 
-- GRANT ALL ON SCHEMA public TO app_user;

-- You can add table creation scripts here if you are not using a migration tool like sqlx-cli
-- For example:
-- CREATE TABLE IF NOT EXISTS users (
-- id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
-- username VARCHAR(255) UNIQUE NOT NULL,
-- email VARCHAR(255) UNIQUE NOT NULL,
-- password_hash TEXT NOT NULL,
-- created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
-- updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
-- );

-- Optional: Insert some seed data
-- INSERT INTO users (username, email, password_hash) VALUES ('testuser', 'test@example.com', crypt('password123', gen_salt('bf'))) ON CONFLICT DO NOTHING;

-- Note: It's generally recommended to use a migration tool (like sqlx-cli for Rust)
-- to manage your schema. This init.sql is best for one-time setup like creating extensions
-- or initial roles if not handled by your application or migration tool. 