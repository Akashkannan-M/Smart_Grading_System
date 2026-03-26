# Smart Grading System

A full-stack application built using Next.js (Frontend), Spring Boot (Backend), and PostgreSQL (Supabase).

## Database Setup
1. Create a Supabase PostgreSQL database project.
2. Provide your supabase credentials as environment variables when running the backend.

## Backend (Spring Boot)
1. Navigate to the `backend` folder: `cd backend`
2. Run with Maven:
```sh
export SUPABASE_JDBC="jdbc:postgresql://aws-1-ap-northeast-2.pooler.supabase.com:6543/postgres?user=postgres.uffydyvsfwuetgivltfr&password=[Akash!314325]"
export SUPABASE_USERNAME="postgres"
export SUPABASE_PASSWORD="password123!"

mvn spring-boot:run
```
3. Initialize the database by doing a GET request to:
`http://localhost:8080/api/setup`
This will populate the database with dummy students, staff, CC, and HOD users.

## Frontend (Next.js)
1. Navigate to the `frontend` directory: `cd frontend`
2. Install dependencies (if not already): `npm install`
3. Start the UI: `npm run dev`
4. Access the web app at: `http://localhost:3000`

## Default Users (Password format: DOB+@role)
- **HOD:** sujatha (01011980@hod)
- **CC:** ayyapan_cc (01011980@cc)
- **Staff:** aarthi (01011980@staff), ayyapan, siva, suga, elambarathi, indu
- **Students:** 2024001, 2024002... 2024025 (01012000@student)

## Deployment (Render & Vercel)
### Render (Backend)
- Push code to GitHub.
- Create a Web Service via Render and point it to the repository's `backend` folder.
- Add `SUPABASE_JDBC`, `SUPABASE_USERNAME`, and `SUPABASE_PASSWORD` to Render Environment Variables.
- Command: `mvn clean package && java -jar target/backend-0.0.1-SNAPSHOT.jar`

### Vercel (Frontend)
- Create a new project pointing to the repository.
- Root directory should be `frontend`.
- Vercel automatically detects Next.js configurations.
