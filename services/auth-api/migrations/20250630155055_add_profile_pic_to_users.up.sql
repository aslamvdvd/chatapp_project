-- services/auth-api/migrations/20250630155055_add_profile_pic_to_users.up.sql
ALTER TABLE users
ADD COLUMN profile_pic TEXT; -- TEXT is a good choice for storing URLs or file paths.