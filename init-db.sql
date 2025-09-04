-- Create database if it doesn't exist
CREATE DATABASE healthcare_qr;

-- Create user if it doesn't exist
DO
$do$
BEGIN
   IF NOT EXISTS (
      SELECT FROM pg_catalog.pg_roles
      WHERE  rolname = 'healthcare_user') THEN

      CREATE ROLE healthcare_user LOGIN PASSWORD 'healthcare_pass';
   END IF;
END
$do$;

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE healthcare_qr TO healthcare_user;