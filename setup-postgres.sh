#!/bin/bash

# Setup PostgreSQL for Healthcare QR Sharing

echo "Setting up PostgreSQL database..."

# Create database and user
psql -U postgres -c "CREATE DATABASE healthcare_qr;"
psql -U postgres -c "CREATE USER healthcare_user WITH PASSWORD 'healthcare_pass';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE healthcare_qr TO healthcare_user;"

echo "Database setup complete!"
echo ""
echo "Connection details:"
echo "Database: healthcare_qr"
echo "Username: healthcare_user"
echo "Password: healthcare_pass"
echo "URL: jdbc:postgresql://localhost:5432/healthcare_qr"
echo ""
echo "To run with PostgreSQL:"
echo "mvn spring-boot:run"
echo ""
echo "To run with H2 (development):"
echo "mvn spring-boot:run -Dspring-boot.run.profiles=dev"