-- Add up migration script here

-- Create the user_role ENUM type
CREATE TYPE user_role AS ENUM ('user', 'admin');

-- Add the role column to the users table
ALTER TABLE users
ADD COLUMN role user_role NOT NULL DEFAULT 'user';
