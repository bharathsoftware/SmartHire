# SmartHire – Job Recruitment Platform

SmartHire is a full-stack job recruitment platform built with **React, Vite, Spring Boot, Spring Security, JWT, Hibernate/JPA, and MySQL**. It provides role-based functionality for Job Seekers, Recruiters, and Administrators.

The project demonstrates full-stack development concepts, user authentication, job management, job applications, resume uploads, and database integration.

## Features

### Job Seeker
- Register and log in securely.
- Browse available job opportunities.
- Apply for jobs.
- Submit educational qualification details.
- Upload a PDF resume.
- Track application status, where implemented.

### Recruiter
- Register and log in as a Recruiter.
- Create and manage job postings, where implemented.
- View applications for their own job postings.
- View applicant details and application counts.
- Download applicant resumes, where implemented.
- Update application statuses, where implemented.

### Admin
- Role-based Admin access and screens.
- Administrative functionality according to the implemented features.

## Tech Stack

| Technology | Purpose |
|---|---|
| Java | Backend programming |
| Spring Boot | REST API development |
| Spring Security | Authentication and authorization |
| JWT | Token-based authentication |
| Spring Data JPA | Database operations |
| Hibernate | ORM and entity management |
| MySQL | Relational database |
| React | Frontend user interface |
| Vite | Frontend development server and build tool |
| JavaScript | Frontend logic |
| HTML and CSS | UI structure and styling |
| Maven | Backend dependency management |
| Git and GitHub | Version control and source code hosting |

## Prerequisites

Install the following before running the project:

- JDK version specified in `backend/pom.xml` (JDK 21 if configured for Java 21)
- Node.js LTS and npm
- MySQL Server 8 or a compatible version
- Git
- VS Code or another code editor

## Project Structure

```text
SmartHire/
├── backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       └── resources/
│   │           └── application.properties
│   ├── uploads/
│   │   └── resumes/
│   ├── pom.xml
│   └── mvnw.cmd
├── frontend/
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── vite.config.js
├── database/
│   └── schema.sql
└── README.md
```

*Note: The folder and file names above are representative. Adjust them to match the actual repository.*

## Installation and Setup

### 1. Clone the Repository

```bash
git clone https://github.com/bharathsoftware/SmartHire.git
cd SmartHire
```

### 2. Create the MySQL Database

Open MySQL Workbench or the MySQL command-line client and execute:

```sql
CREATE DATABASE IF NOT EXISTS smarthire;
```

Confirm that the database exists:

```sql
SHOW DATABASES;
USE smarthire;
```

If your project includes `database/schema.sql`, review it and execute it only if needed by your setup. Hibernate may create or update tables automatically, depending on the JPA configuration.

### 3. Configure Backend Database Credentials

Open PowerShell in the project directory and set the database environment variables:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/smarthire"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "YOUR_MYSQL_PASSWORD"
```

Replace `YOUR_MYSQL_PASSWORD` with your own local MySQL password. Do not publish your real password or commit it to GitHub.

Move to the backend directory:

```powershell
cd .\backend
```

Start Spring Boot using the Maven wrapper, if available:

```powershell
.\mvnw.cmd spring-boot:run
```

Alternatively, if Maven is installed and configured:

```powershell
mvn spring-boot:run
```

The backend normally runs at:

`http://localhost:8080`

**Important:** The environment variable names must match the placeholders used in your `application.properties` or `application.yml`. If your configuration uses different names, update these commands accordingly.

### 4. Start the React Frontend

Open a **second terminal** in the SmartHire project root:

```powershell
cd .\frontend
npm install
npm run dev
```

Open the local URL printed in the terminal. With the default Vite configuration, it is usually:

`http://localhost:5173`

Keep both the backend and frontend running while testing the application.

## How to Test the Application

1. Open the frontend in your browser.
2. Register a new Job Seeker account.
3. Log in and browse the available jobs.
4. Register a separate Recruiter account using a different email address.
5. Log in as the Recruiter and create a job posting.
6. Log back in as the Job Seeker and apply for that job.
7. If supported, provide the required qualification details and upload a PDF resume.
8. Log in as the Recruiter and inspect the applications for the job you posted.
9. Test applicant counts, resume downloads, and application status updates if those features are implemented.

Use accounts you create yourself. Do not assume a default demo account or password exists.

## Main API Endpoints

The following endpoints describe the expected API surface; verify the actual controller mappings in the source code.

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/users` | Register a user |
| POST | `/api/users/login` | Authenticate a user |
| GET | `/api/users/me` | Get the current user's details |
| GET | `/api/jobs` | List available jobs |
| POST | `/api/jobs` | Create a job posting |
| GET | `/api/recruiters/my-jobs` | Retrieve the recruiter's jobs |
| GET | `/api/recruiters/jobs/{jobId}/applications` | View applicants for a job |
| GET | `/api/recruiters/applications/{applicationId}/resume` | Download an applicant's resume |
| POST | `/api/job-seeker/resume/upload` | Upload a PDF resume |
| POST | `/api/job-seekers/apply/{jobId}` | Apply for a job |
| PUT | `/api/applications/{id}/status?status=SHORTLISTED` | Update application status |

Endpoints may differ depending on the current implementation. Check the backend controllers and security configuration before relying on this list.

## Security

SmartHire uses Spring Security and JWT-based authentication, according to the project configuration.

Important security considerations:

- Keep database credentials private.
- Protect recruiter operations so recruiters can access only their own job applications.
- Validate uploaded files and enforce file-size limits.
- Store uploaded resumes outside publicly accessible static directories.
- Validate user input on the backend.
- Configure CORS appropriately for the frontend origin.
- Never commit private keys, tokens, passwords, or other secrets.

Before production deployment, add and verify appropriate password recovery, email verification, rate limiting, HTTPS, logging, automated tests, and production-grade secret management.

## Troubleshooting

### MySQL Connection Failed
- Confirm that MySQL Server is running.
- Check that the `smarthire` database exists.
- Verify the username, password, JDBC URL, and environment-variable placeholders.
- Confirm that the MySQL port is correct.

### Backend Does Not Start
- Check the required Java version.
- Confirm that Maven dependencies can be downloaded.
- Review the error messages in the terminal.
- Make sure port `8080` is not already occupied.

### Frontend Does Not Start
- Confirm Node.js and npm are installed.
- Run `npm install` inside the `frontend` directory.
- Check the error output from `npm run dev`.
- Confirm the frontend's API configuration points to the correct backend URL.

### Login or Authorization Fails
- Confirm that the account was registered successfully.
- Use the correct email and password.
- Check the JWT handling and Spring Security configuration.
- Confirm that the account has the role required for the operation.

### Resume Upload or Download Fails
- Confirm that the uploaded file is a PDF and meets the configured size limit.
- Check the resume storage directory and backend write permissions.
- Verify that the download endpoint checks authorization before returning a file.

## Learning Objectives

This project helps demonstrate practical experience with:

- Full-stack web application development
- React components and frontend state
- REST API integration
- Spring Boot application structure
- Spring Security and JWT authentication
- Role-based authorization
- JPA/Hibernate and relational database integration
- Job posting and application workflows
- File uploads and protected file downloads
- Git and GitHub version control

## Developer

**Bharathkumar Y**

- GitHub: https://github.com/bharathsoftware
- Project Repository: https://github.com/bharathsoftware/SmartHire

## Project Status

SmartHire is a learning project under development. Refer to the current source code to determine which features are complete and which require further implementation and testing.

---

