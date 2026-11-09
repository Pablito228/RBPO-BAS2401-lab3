-- Run as a PostgreSQL administrator after creating database tourism_rental.
-- Replace the passwords before execution; do not commit real credentials.
-- rental_app is used by the application; rental_readonly is for reporting and checks.
CREATE ROLE rental_app LOGIN PASSWORD 'replace-with-strong-password' NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION;
CREATE ROLE rental_readonly LOGIN PASSWORD 'replace-with-strong-password' NOSUPERUSER NOCREATEDB NOCREATEROLE NOREPLICATION;
GRANT CONNECT ON DATABASE tourism_rental TO rental_app, rental_readonly;
\c tourism_rental
GRANT USAGE, CREATE ON SCHEMA public TO rental_app;
GRANT USAGE ON SCHEMA public TO rental_readonly;

-- Application account: CRUD access and sequence access, but no administrative privileges.
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO rental_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO rental_app;
ALTER DEFAULT PRIVILEGES FOR ROLE rental_app IN SCHEMA public
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO rental_app;
ALTER DEFAULT PRIVILEGES FOR ROLE rental_app IN SCHEMA public
    GRANT USAGE, SELECT ON SEQUENCES TO rental_app;

-- Read-only account: can inspect data, but cannot change it or create database objects.
GRANT SELECT ON ALL TABLES IN SCHEMA public TO rental_readonly;
ALTER DEFAULT PRIVILEGES FOR ROLE rental_app IN SCHEMA public
    GRANT SELECT ON TABLES TO rental_readonly;
