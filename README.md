# Job Application Tracker API

A secure Spring Boot REST API for managing job applications with JWT authentication, multi-user isolation, statistics, and follow-up tracking.

---

## Features
- User registration & login (JWT-based authentication)

- Multi-user data isolation

- CRUD operations for job applications

- Search, sorting, and pagination

- Status history tracking

- Follow-up reminder system

- Overview statistics (interview & acceptance rates)

- Global exception handling

- Integration testing with MockMvc
  
  ---

## Tech Stack
- **Java 17**

- **Spring Boot**

- **Spring Security (JWT)**

- **Spring Data JPA**

- **MySQL**

- **JUnit 5 & MockMvc**
---
## Authentication Flow
### Register

```
POST /auth/register
```
### Login

```
POST /auth/login
```
### Use JWT Token
```
Authorization: Bearer <your_token>
```

---
## Core Features

### Authentication & Security

- User registration & login

- Stateless JWT authentication

- Password hashing with BCrypt

- Multi-user data isolation

- Protected endpoints

### Job Application Management

- Create application

- Get application by ID

- Update status

- Delete application

- Pagination & sorting

- Search by company/title/location

 ### Status History Tracking

- Each status change is recorded and can be retrieved via:
```
GET /api/jobs/{id}/history
```
### Follow-up Reminder System

Identify applications that haven't been updated in 14 days:
```
GET /api/jobs/applications/followups?days=14
```
### Statistics & Funnel Metrics
```
GET /api/jobs/stats
```

Returns:

- Total applications

- Accepted / Rejected / Draft

- Interview rate
 - Acceptance rate

Statistics are calculated per user.

---
▶️ Running the Project

1️⃣ Configure MySQL in application.properties
```
spring.datasource.url=jdbc:mysql://localhost:3306/your_db
spring.datasource.username=root
spring.datasource.password=your_password
```

2️⃣ Run the application
```
mvn spring-boot:run
```

3️⃣ Open Swagger UI
```

http://localhost:8080/swagger-ui/index.html
```
---
🧱 Architecture Overview

Layered architecture:

Controller (REST endpoints)

Service (business logic)

Repository (JPA)

Security (JWT filter + UserDetailsService)

Global exception handling

DTO-based API contract

---

📈 What This Project Demonstrates

✔ Secure backend development

✔ RESTful API design

✔ Database modeling

✔ Authentication & authorization

✔ Clean architecture principles


✔ Real-world business logic

---
📬 Future Improvements

- Docker deployment

- Role-based authorization (Admin/User)

