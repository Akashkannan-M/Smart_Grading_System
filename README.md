# Smart Grading System

A full-stack, production-ready web application for Sudharsan Engineering College.

## Features
- Role-based Dashboards (HOD, CC, Staff, Student)
- Rank visualization with Medals
- JWT Authentication

## Tech Stack
**Frontend:** React (Vite), Tailwind CSS
**Backend:** Spring Boot (Java 17)
**Database:** PostgreSQL (Supabase)

## Environment Variables (.env files)

### Backend (in `application.properties`/Environment)
```properties
DB_URL=jdbc:postgresql://your-supabase-url...
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=86400000
FRONTEND_URL=https://your-frontend-domain.com
```

### Frontend (in `.env.local`)
```env
VITE_API_BASE_URL=https://your-backend-domain.com/api
```

## Running Locally
1. Start PostgreSQL DB 
2. `cd backend && ./mvnw spring-boot:run`
3. `cd frontend && npm run dev`

## Deployment
- Backend: Deploy on Render connecting to Supabase database.
- Frontend: Deploy on Vercel setting the environment variable `VITE_API_BASE_URL`.
