-- Add down migration script here

DROP INDEX IF EXISTS idx_users_username;
DROP INDEX IF EXISTS idx_users_email;
DROP TABLE IF EXISTS users;

-- Note: Dropping extensions like "uuid-ossp" is often skipped in down migrations
-- unless it was exclusively created for this table and is not used by anything else.
-- If you want to be extremely thorough and it's safe:
-- DROP EXTENSION IF EXISTS "uuid-ossp"; 
-- However, this can fail if other objects depend on it.
