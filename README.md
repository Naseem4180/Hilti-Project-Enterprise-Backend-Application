# Hilti Booking System

This repository contains a complete working MVP for the Hilti Singapore Anchor Testing Booking & Field Management System.

## Structure

- `backend/` — Spring Boot REST API with MySQL persistence and JWT auth.
- `frontend/` — React web app for customers, field executives, and admin users.

## Setup

### Backend

The backend runs by default using an embedded H2 database, so MySQL is not required for local development.

1. Install Java 17+.
2. Run:
   ```bash
   cd backend
   mvn clean spring-boot:run
   ```

If you want to use MySQL instead, update the `mysql` profile in `backend/src/main/resources/application.yml` and start with:

```bash
cd backend
mvn -Dspring-boot.run.profiles=mysql clean spring-boot:run
```

### Frontend

1. Install Node.js 18+.
2. Run:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

## Default accounts

- Admin: `admin@hilti.com` / `Admin123!`
- Customer: `customer@hilti.com` / `Customer123!`
- FE: `fe@hilti.com` / `Fe123!`
- Manager: `manager@hilti.com` / `Manager123!`

## Notes

- The backend exposes API under `/api`.
- The frontend proxies calls to the backend during development.
- The project is designed for a local, no-Docker environment.
