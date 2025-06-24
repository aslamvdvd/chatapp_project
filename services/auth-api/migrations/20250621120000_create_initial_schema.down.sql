-- Revert initial database schema

-- Drop tables in reverse order of creation due to dependencies
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS friend_requests;
DROP TABLE IF EXISTS users;

-- Drop the enum types
DROP TYPE IF EXISTS friend_request_status;
DROP TYPE IF EXISTS user_role; 