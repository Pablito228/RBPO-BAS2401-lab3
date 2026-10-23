-- Run as a PostgreSQL administrator after creating database tourism_rental.
-- Replace the password before execution; do not commit real credentials.
CREATE ROLE rental_app LOGIN PASSWORD 'replace-with-strong-password' NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION;
GRANT CONNECT ON DATABASE tourism_rental TO rental_app;
\c tourism_rental
GRANT USAGE, CREATE ON SCHEMA public TO rental_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO rental_app;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT USAGE, SELECT ON SEQUENCES TO rental_app;

