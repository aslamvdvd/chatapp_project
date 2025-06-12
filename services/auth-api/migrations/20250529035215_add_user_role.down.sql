-- Add down migration script here

-- Remove the role column from the users table
ALTER TABLE users
DROP COLUMN IF EXISTS role;

-- Drop the user_role ENUM type
DROP TYPE IF EXISTS user_role;
