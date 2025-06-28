# Project Issues & TODOs

## 🐛 Bugs

### Issue #1: Middle name not being registered in the database
**Status:** Open
**Priority:** High
**Description:**
When a new user signs up, the `middle_name` field is passed in the request payload,
but it's not being correctly stored in the 'users' table. The column exists,
but the value is consistently NULL or empty after insertion.
**Steps to reproduce:**
1. Register a new user via the `/auth/signup` endpoint.
2. Include a value for `middle_name` in the request body.
3. Check the 'users' table in the database for the new user's `middle_name`.
**Affected component(s):** auth-api, database insertion logic
**Resolution:** (Leave blank until fixed)

### Issue #2: Leading/trailing spaces in login credentials not cleaned
**Status:** Open
**Priority:** Medium
**Description:**
Users can inadvertently enter leading or trailing spaces in their username or
email when logging in, which prevents successful authentication. The system
should automatically trim these spaces before validation and lookup.
**Affected component(s):** auth-api, login endpoint
**Resolution:**

## ✨ Features / Enhancements

### Feature #3: Implement password reset functionality
**Status:** To Do
**Priority:** High
**Description:**
Develop an endpoint and associated logic for users to reset forgotten passwords.
This will likely involve email verification and token-based reset links.
**Affected component(s):** auth-api, email service (future), database (tokens)
**Resolution:**

## ✅ Completed

### Issue #0: Fixed the docker and database schema initialization conflict
**Status:** Closed
**Priority:** Critical
**Description:**
The 'users' table was either missing or lacked required columns (first_name, last_name, date_of_birth)
due to a conflict between `infra/postgres/init.sql` and the 'sqlx' migrations,
and an incorrect volume mount in `docker-compose.yaml` for the migrations service.
**Resolution:**
- Removed application-specific table creation from `init.sql`.
- Corrected migrations service volume mount to `./services/auth-api/migrations`.
- Required `docker compose down -v` and `up --build -d` for full refresh.
**Date Completed:** 2025-06-28