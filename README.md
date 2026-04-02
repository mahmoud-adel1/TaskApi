# Task API

A Spring Boot task management REST API with JWT authentication, role-based authorization, JPA persistence, and OpenAPI documentation.

The application supports:

- User registration and login
- JWT-based stateless authentication
- Role-based access control for `ADMIN` and `USER`
- Task assignment to users
- Task status tracking (`OPEN`, `DONE`)
- H2 in-memory database for local development
- Swagger UI / OpenAPI docs

## Tech Stack

- Java 25
- Spring Boot 4.0.5
- Spring Web MVC
- Spring Security
- Spring Data JPA
- Hibernate
- H2 Database
- MySQL Connector
- JWT (`jjwt`)
- Lombok
- Springdoc OpenAPI
- Maven Wrapper

## Project Structure

```text
src/main/java/com/example/task
├── config        # startup seeding
├── controller    # REST controllers
├── dto           # request/response payloads
├── entity        # JPA entities
├── enums         # role and task status enums
├── exception     # custom exceptions and global handler
├── mapper        # DTO/entity mapping helpers
├── repository    # Spring Data repositories
├── security      # JWT and Spring Security configuration
├── service       # business logic
└── TaskApplication.java
```

## Features

### Authentication

- `POST /auth/register` creates a new user account
- `POST /auth/login` authenticates a user and returns a JWT
- JWT contains:
  - subject = username
  - claim `role`

### Authorization

- Public endpoints:
  - `/auth/**`
  - `/swagger-ui/**`
  - `/swagger-ui.html`
  - `/v3/api-docs/**`
  - `/h2-console/**`
- Protected endpoints require a valid bearer token
- Admin-only operations use method security with `@PreAuthorize("hasRole('ADMIN')")`

### Task Management

- Admin can create tasks for a specific user
- Admin can mark tasks as done
- Admin can delete tasks
- Any authenticated user can fetch only their assigned tasks

## Default Runtime Configuration

Current configuration from `src/main/resources/application.yaml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password: ''
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

jwt:
  expiration-minutes: 60
```

Notes:

- The app currently runs against an in-memory H2 database by default.
- MySQL dependency is present, but MySQL is not currently configured in `application.yaml`.
- Data is recreated on each restart because the configured H2 database is in-memory.

## Seeded Data

On startup, the app seeds roles and two users if the role table is empty:

- Admin user:
  - username: `admin`
  - password: `admin123`
- Regular user:
  - username: `mahmoud`
  - password: `user123`

It also creates two roles:

- `ADMIN`
- `USER`

## Getting Started

### Prerequisites

- JDK 25 installed
- Internet access on the first Maven build to download dependencies

### Run the Application

If `mvnw` is executable:

```bash
./mvnw spring-boot:run
```

If the wrapper script is not executable:

```bash
bash mvnw spring-boot:run
```

The API starts on:

```text
http://localhost:8080
```

### Run Tests

```bash
bash mvnw test
```

## API Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

H2 Console:

```text
http://localhost:8080/h2-console
```

Suggested H2 settings:

- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: empty

## Authentication Flow

1. Register a new account or log in with an existing account.
2. Copy the returned JWT token.
3. Send it in the `Authorization` header:

```http
Authorization: Bearer <token>
```

## API Endpoints

### Auth

#### Register

`POST /auth/register`

Request:

```json
{
  "name": "Mahmoud",
  "username": "mahmoud2",
  "password": "user123"
}
```

Response:

```json
{
  "username": "mahmoud2",
  "token": "<jwt-token>"
}
```

#### Login

`POST /auth/login`

Request:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

Response:

```json
{
  "username": "admin",
  "token": "<jwt-token>"
}
```

### Tasks

#### Create Task

`POST /tasks`

Authorization: `ADMIN` only

Request:

```json
{
  "title": "Prepare API docs",
  "description": "Document the authentication and task endpoints",
  "username": "mahmoud"
}
```

Response:

```json
{
  "id": 1,
  "title": "Prepare API docs",
  "description": "Document the authentication and task endpoints",
  "TaskStatus": "OPEN",
  "username": "mahmoud"
}
```

#### Get Assigned Tasks

`GET /tasks`

Authorization: any authenticated user

Response:

```json
[
  {
    "id": 1,
    "title": "Prepare API docs",
    "description": "Document the authentication and task endpoints",
    "TaskStatus": "OPEN",
    "username": "mahmoud"
  }
]
```

Behavior:

- Returns only tasks assigned to the currently authenticated user

#### Mark Task as Done

`PUT /tasks/{taskId}`

Authorization: `ADMIN` only

Response:

```json
{
  "id": 1,
  "title": "Prepare API docs",
  "description": "Document the authentication and task endpoints",
  "TaskStatus": "DONE",
  "username": "mahmoud"
}
```

Important:

- This endpoint does not currently accept a request body.
- The current implementation simply changes the task status to `DONE`.

#### Delete Task

`DELETE /tasks/{taskId}`

Authorization: `ADMIN` only

Response:

- `200 OK` with empty body

## Example cURL Usage

### Login as Admin

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### Create a Task

```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin-token>" \
  -d '{
    "title": "Finish README",
    "description": "Write project documentation",
    "username": "mahmoud"
  }'
```

### Get Current User Tasks

```bash
curl http://localhost:8080/tasks \
  -H "Authorization: Bearer <user-token>"
```

### Mark a Task as Done

```bash
curl -X PUT http://localhost:8080/tasks/1 \
  -H "Authorization: Bearer <admin-token>"
```

### Delete a Task

```bash
curl -X DELETE http://localhost:8080/tasks/1 \
  -H "Authorization: Bearer <admin-token>"
```

## Error Handling

The project uses a global exception handler and returns a structured error payload similar to:

```json
{
  "status": 409,
  "error": "Conflict",
  "message": "1 is not found",
  "path": "/tasks/1",
  "timestamp": "2026-04-02T20:00:00Z"
}
```

Mapped exceptions currently include:

- `UserNotFoundException`
- `UsernameAlreadyExistsException`
- `TaskNotFoundException`
- `InvalidUserNameOrPassword`
- generic fallback `Exception`

## Data Model

### User

- `id`
- `name`
- `username`
- `password`
- `role`
- `tasks`

### Role

- `id`
- `roleType` (`ADMIN`, `USER`)

### Task

- `id`
- `title`
- `description`
- `taskStatus` (`OPEN`, `DONE`)
- `user`

## Security Notes

- Passwords are stored encoded with Spring Security's password encoder.
- Sessions are stateless.
- Requests are authenticated through a JWT filter.
- H2 Console is publicly accessible in the current configuration for local development convenience.
- JWT secret is currently stored directly in `application.yaml`; for production this should move to environment-based configuration.

## Known Implementation Notes

- `POST /auth/login` currently returns HTTP `201 Created`; many APIs would use `200 OK`.
- `PUT /tasks/{taskId}` only marks a task as `DONE`; it is not a general update endpoint.
- The response DTO uses `TaskStatus` with an uppercase field name, because that is how it is defined in code.
- Startup seeding prints a generated JWT key to the console, even though the application already reads its configured key from `application.yaml`.
- Only one automated test currently exists, and it verifies that the Spring application context loads.

## Verification

Verified in this repository on April 2, 2026:

- `bash mvnw test`
- Result: `BUILD SUCCESS`
- Tests run: `1`
- Failures: `0`
- Errors: `0`
- Skipped: `0`

## Future Improvements

- Add endpoint-level tests for auth and task flows
- Add pagination/filtering for task retrieval
- Support MySQL configuration profiles
- Move secrets to environment variables
- Improve task update semantics with a request body
- Add refresh token support
- Add Docker support

## License

No license is currently declared in `pom.xml` or the repository.
