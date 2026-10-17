# SmartHire — Java Full-Stack Recruitment Platform

SmartHire is a learning project built with React + Vite, Spring Boot, Spring Security/JWT, JPA/Hibernate and MySQL. It includes Job Seeker, Recruiter and Admin role-based screens.

## Requirements
- JDK 17 or the Java version specified in `backend/pom.xml`
- Node.js LTS and npm
- MySQL Server running locally
- VS Code (recommended)

## 1. Create the database
In MySQL Workbench or the MySQL CLI, run:

```sql
CREATE DATABASE smarthire;
```

## 2. Configure the backend database credentials
The project intentionally does not ship with a real database password. Open PowerShell and set the credentials in the same terminal where you start Spring Boot:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/smarthire"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "YOUR_MYSQL_PASSWORD"
cd .\backend
.\mvnw.cmd spring-boot:run
```

Replace `YOUR_MYSQL_PASSWORD` with your own MySQL password. Keep it private and do not commit it to GitHub. The database tables are created/updated by Hibernate. Uploaded PDF files are stored in `backend/uploads/resumes` by default; MySQL stores the file metadata. Set `RESUME_STORAGE_DIR` to change the folder.

## 3. Start the React frontend
Open a second terminal:

```powershell
cd .\frontend
npm install
npm run dev
```

Open the local Vite URL shown in the terminal, normally `http://localhost:5173`.

## 4. Create accounts and test both roles
1. Open **Get Started**.
2. Create a **Job Seeker** account with an email and password you control.
3. Log in as the Job Seeker, open Find Jobs, and apply to a job posted by a recruiter.
4. Log out.
5. Open **Get Started** again and choose **Recruiter**. Register a different email/password.
6. Log in as that Recruiter. Post a job. The recruiter dashboard shows applications only for jobs owned by that recruiter.
7. Log out and sign in as the Job Seeker to apply to that job. The application form asks for degree/qualification and a PDF resume (maximum 5 MB); the account name and email are used automatically.
8. Log back in as the Recruiter. Applicant counts come from MySQL, View Applicants shows each applicant's name, email, degree, application status and date, and the recruiter can download the PDF resume for their own job.

**Important:** `recruiter@gmail.com` is not a built-in demo account. It only works if you previously registered it with the exact password. If you don't know that password, register a new Recruiter account with an email you control.

## Main API
- `POST /api/users` — registration
- `POST /api/users/login` — returns a JWT
- `GET /api/users/me` — current signed-in user
- `GET /api/jobs` — public job listing
- `POST /api/jobs` — Recruiter-only job posting
- `GET /api/recruiters/my-jobs` — signed-in recruiter's jobs with database-backed applicant counts
- `GET /api/recruiters/jobs/{jobId}/applications` — applicant details for a job owned by the recruiter
- `GET /api/recruiters/applications/{applicationId}/resume` — securely downloads the applicant PDF for the job owner
- `POST /api/job-seeker/resume/upload` — uploads a PDF resume and saves degree/profile and resume metadata
- `POST /api/job-seekers/apply/{jobId}` — Job Seeker applies
- `PUT /api/applications/{id}/status?status=SHORTLISTED` — Recruiter updates an application status

## Troubleshooting
- **Invalid email or password:** confirm the account exists and the password is exactly the one used at registration. Role selection on the register page is what sets the account role; there is no default recruiter password.
- **Cannot connect to MySQL:** confirm MySQL is running, database `smarthire` exists, and the environment variables above are set in the same terminal.
- **Port 8080 is in use:** stop the other backend process or change `server.port` in `application.properties`.
- **Frontend cannot call the API:** make sure Spring Boot is running on port 8080 and Vite is running on port 5173.

## Security notes
This is a local development project, not production-ready deployment configuration. Before production, add email verification, password reset, stronger validation, rate limiting, secret management, HTTPS, tests, and deployment-specific CORS settings.
