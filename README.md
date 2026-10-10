# Personal Finance Manager

A RESTful Personal Finance Manager API built using Java 17 and Spring Boot.

The application allows users to securely manage transactions, categories, savings goals, and financial reports. Authentication is session-based, and all user-specific financial data is isolated between users.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- H2 Database
- Maven
- JUnit 5
- Mockito
- JaCoCo
- Docker
- Render

---

## Features

### Authentication

- User registration
- User login
- Session-based authentication
- BCrypt password hashing
- Session ID rotation after successful login
- Logout and session invalidation
- Protected API endpoints
- User-level data isolation

### Transactions

Users can:

- Create income and expense transactions
- View transactions
- Filter transactions
- Update transactions
- Delete transactions

Transaction validation includes:

- Positive transaction amount
- Maximum of two decimal places for monetary values
- Valid transaction date
- No future-dated transactions
- Valid and accessible category
- Immutable transaction date during updates
- Input length validation before persistence

### Categories

The application provides the following default categories.

#### Income

- Salary

#### Expense

- Food
- Rent
- Transportation
- Entertainment
- Healthcare
- Utilities

Users can also:

- Create custom categories
- View default and custom categories
- Delete unused custom categories

Default categories cannot be deleted.

A custom category referenced by a transaction cannot be deleted.

Custom category names are unique per user. Uniqueness is checked by the application and also enforced at the database level.

### Savings Goals

Users can:

- Create savings goals
- View savings goals
- Update target amount
- Update target date
- Delete savings goals

Target amounts must be positive and support a maximum of two decimal places.

Goal progress is calculated using:

```text
Current Progress = Total Income - Total Expenses
```

Only transactions occurring on or after the goal's start date are considered.

The API also calculates:

- Current progress
- Progress percentage
- Remaining amount

If no start date is supplied, the current date is used.

### Financial Reports

The API supports:

- Monthly reports
- Yearly reports
- Income grouped by category
- Expenses grouped by category
- Net savings calculation

Net savings is calculated as:

```text
Net Savings = Total Income - Total Expenses
```

Deleted transactions are automatically excluded from reports and savings-goal calculations.

---

## Project Architecture

The application follows a layered Spring Boot architecture:

```text
Client
  |
  v
Controller
  |
  v
Service
  |
  v
Repository
  |
  v
H2 Database
```

### Controller Layer

Handles HTTP requests, validation, authentication context, and responses.

```text
controller/
```

### Service Layer

Contains business logic, ownership checks, validation rules, calculations, and application behavior.

```text
service/
```

### Repository Layer

Uses Spring Data JPA for database access.

```text
repository/
```

### Entity Layer

Contains JPA entities representing persisted application data.

```text
entity/
```

### DTO Layer

Defines request and response objects used by the REST API.

```text
dto/
```

### Exception Handling

Application exceptions are handled globally using Spring's `@RestControllerAdvice`.

The API uses appropriate HTTP status codes including:

- `200 OK`
- `201 Created`
- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`
- `404 Not Found`
- `409 Conflict`

---

# API Endpoints

Base path:

```text
/api
```

## Authentication

### Register

```http
POST /api/auth/register
```

Example request:

```json
{
  "username": "john@example.com",
  "password": "securePassword123",
  "fullName": "John Doe",
  "phoneNumber": "+1234567890"
}
```

Example response:

```json
{
  "message": "User registered successfully",
  "userId": 1
}
```

### Login

```http
POST /api/auth/login
```

Example request:

```json
{
  "username": "john@example.com",
  "password": "securePassword123"
}
```

Example response:

```json
{
  "message": "Login successful"
}
```

Authentication uses an HTTP session cookie.

After successful authentication, an existing session ID is rotated before the authenticated security context is persisted.

### Logout

```http
POST /api/auth/logout
```

Example response:

```json
{
  "message": "Logout successful"
}
```

Logout invalidates the current HTTP session.

---

## Categories

### Get Categories

```http
GET /api/categories
```

Returns default categories and custom categories belonging to the authenticated user.

### Create Category

```http
POST /api/categories
```

Example request:

```json
{
  "name": "Freelance",
  "type": "INCOME"
}
```

Allowed category types:

```text
INCOME
EXPENSE
```

Category names must be unique for each user.

### Delete Category

```http
DELETE /api/categories/{name}
```

Only unused custom categories belonging to the authenticated user can be deleted.

Default categories cannot be deleted.

---

## Transactions

### Create Transaction

```http
POST /api/transactions
```

Example request:

```json
{
  "amount": 5000.0,
  "date": "2026-10-01",
  "category": "Salary",
  "description": "October Salary"
}
```

### Get Transactions

```http
GET /api/transactions
```

Supported optional filters:

```text
startDate
endDate
categoryId
category
type
```

Example:

```http
GET /api/transactions?startDate=2026-10-01&endDate=2026-10-31
```

Category-name filter:

```http
GET /api/transactions?category=Salary
```

Transactions are returned newest first.

### Update Transaction

```http
PUT /api/transactions/{id}
```

Example request:

```json
{
  "amount": 5500.0,
  "description": "Updated salary"
}
```

The transaction date cannot be modified.

### Delete Transaction

```http
DELETE /api/transactions/{id}
```

---

## Savings Goals

### Create Goal

```http
POST /api/goals
```

Example request:

```json
{
  "goalName": "Emergency Fund",
  "targetAmount": 10000.0,
  "targetDate": "2027-12-31",
  "startDate": "2026-10-01"
}
```

`startDate` is optional and defaults to the current date.

### Get All Goals

```http
GET /api/goals
```

### Get Goal

```http
GET /api/goals/{id}
```

### Update Goal

```http
PUT /api/goals/{id}
```

Example:

```json
{
  "targetAmount": 15000.0
}
```

Only the target amount and target date are updateable.

### Delete Goal

```http
DELETE /api/goals/{id}
```

---

## Reports

### Monthly Report

```http
GET /api/reports/monthly/{year}/{month}
```

Example:

```http
GET /api/reports/monthly/2026/10
```

Example response:

```json
{
  "month": 10,
  "year": 2026,
  "totalIncome": {
    "Salary": 5000.0
  },
  "totalExpenses": {
    "Food": 500.0
  },
  "netSavings": 4500.0
}
```

### Yearly Report

```http
GET /api/reports/yearly/{year}
```

Example:

```http
GET /api/reports/yearly/2026
```

---

# Running the Application Locally

## Prerequisites

Install:

- Java 17
- Git

The project includes the Maven Wrapper, so a separate Maven installation is not required.

Verify Java:

```bash
java -version
```

The application is built for Java 17.

## Clone the Repository

```bash
git clone https://github.com/Sattei/personal-finance-manager.git
cd personal-finance-manager
```

On Linux/macOS, if required:

```bash
chmod +x mvnw
chmod +x financial_manager_tests.sh
```

## Run Tests

Windows:

```powershell
.\mvnw.cmd clean test
```

Linux/macOS:

```bash
./mvnw clean test
```

## Start the Application

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Local application URL:

```text
http://localhost:8080
```

Local API base URL:

```text
http://localhost:8080/api
```

---

# Running with Docker

The project includes a multi-stage `Dockerfile` for containerized deployment.

## Build the Docker Image

```bash
docker build -t personal-finance-manager .
```

## Run the Container

```bash
docker run --name finance-api --rm \
  -p 8080:8080 \
  -e SESSION_COOKIE_SECURE=false \
  personal-finance-manager
```

The API will be available at:

```text
http://localhost:8080/api
```

Run the supplied E2E test suite against the Docker container:

```bash
bash financial_manager_tests.sh http://localhost:8080/api
```

---

# Database

The application uses an in-memory H2 database.

```text
jdbc:h2:mem:financedb
```

The database is recreated whenever the application restarts.

The H2 browser console is disabled in the production configuration.

---

# Security

The application uses Spring Security with session-based authentication.

Security features include:

- BCrypt password hashing
- HTTP session authentication
- Session ID rotation after successful login
- HttpOnly session cookies
- Secure cookies in production
- Protected application endpoints
- User-level resource ownership validation
- Session invalidation on logout
- Password input validation before BCrypt encoding

Only the following endpoints are publicly accessible:

```text
POST /api/auth/register
POST /api/auth/login
```

All other application endpoints require authentication.

For HTTPS deployment, the following environment variable is configured:

```text
SESSION_COOKIE_SECURE=true
```

This results in the session cookie being marked `Secure`, meaning it is transmitted only over HTTPS.

For local HTTP development, the default is:

```text
SESSION_COOKIE_SECURE=false
```

---

# Validation

Request DTOs use Jakarta Bean Validation.

Examples of enforced validation include:

- Required registration fields
- Valid email-format username
- Valid phone number
- Password length validation
- Positive monetary values
- Maximum two decimal places for monetary values
- Non-future transaction dates
- Future savings-goal target dates
- Valid category type
- Category and description length limits
- Goal-name length limits
- Valid report year/month values

Invalid client input is returned as a `400 Bad Request` instead of being passed directly to persistence or other underlying components.

---

# Testing

The project uses:

- JUnit 5
- Mockito
- Spring Boot Test
- JaCoCo

Run the complete unit test suite:

```bash
./mvnw clean test
```

Current unit-test result:

```text
Tests: 65
Failures: 0
Errors: 0
```

## End-to-End Tests

The supplied assessment E2E script can be executed against a running local API:

```bash
bash financial_manager_tests.sh http://localhost:8080/api
```

Validated local result:

```text
Total Tests Executed: 86
Tests Passed: 86
Tests Failed: 0
Success Rate: 100%
```

---

# Code Coverage

JaCoCo is used for code coverage.

Generate the coverage report with:

```bash
./mvnw clean test
```

The generated report is available at:

```text
target/site/jacoco/index.html
```

The project achieves code coverage above the assessment's required 80% threshold.

---

# JavaDoc

Public application classes and methods are documented using JavaDoc.

Generate JavaDoc with:

```bash
./mvnw javadoc:javadoc
```

Generated documentation is available at:

```text
target/site/apidocs/index.html
```

---

# Production Configuration

The server port is configured as:

```properties
server.port=${PORT:8080}
```

This allows hosting platforms such as Render to provide the runtime port through the `PORT` environment variable.

Session-cookie security is configured using:

```properties
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=${SESSION_COOKIE_SECURE:false}
server.servlet.session.cookie.same-site=lax
```

For the production deployment:

```text
SESSION_COOKIE_SECURE=true
```

---

# Docker Configuration

The application uses a multi-stage Docker build.

The build stage:

- Uses Java 17
- Runs Maven through the Maven Wrapper
- Creates the Spring Boot JAR

The runtime stage:

- Uses a Java 17 runtime image
- Copies only the generated application JAR
- Runs the application using:

```text
java -jar app.jar
```

The `.dockerignore` file prevents unnecessary local files, build output, Git metadata, logs, and environment files from being included in the Docker build context.

---

# Deployment

The application is deployed on Render using Docker.

Render connects directly to the GitHub repository and builds the application using the repository's `Dockerfile`.

## Live Deployment

Application URL:

```text
https://personal-finance-manager-c8w1.onrender.com/
```

API base URL:

```text
https://personal-finance-manager-c8w1.onrender.com/api
```

## Render Configuration

Deployment runtime:

```text
Docker
```

Dockerfile:

```text
./Dockerfile
```

Production environment variable:

```text
SESSION_COOKIE_SECURE=true
```

The application does not hard-code Render's runtime port. Instead it uses:

```properties
server.port=${PORT:8080}
```

## Validate the Deployed API

After Render finishes deploying the latest commit, run:

```bash
bash financial_manager_tests.sh https://personal-finance-manager-c8w1.onrender.com/api
```

Expected result:

```text
Total Tests Executed: 86
Tests Passed: 86
Tests Failed: 0
Success Rate: 100%
```

---

# Design Decisions

## Session-Based Authentication

Session-based authentication is used instead of JWT because the assessment specifies server-side session authentication.

The authenticated Spring Security context is stored in the HTTP session.

An existing session ID is rotated after successful login to protect against session-fixation attacks.

## BCrypt Password Hashing

User passwords are never stored as plain text.

Passwords are hashed using BCrypt before persistence.

Input is validated before reaching the BCrypt encoder.

## BigDecimal for Monetary Values

`BigDecimal` is used for monetary values to avoid floating-point precision errors.

Monetary request values are limited to two decimal places so API validation is consistent with database persistence.

## Category Uniqueness

Custom category uniqueness is checked at the service layer for normal requests.

A database uniqueness constraint also protects against concurrent requests attempting to create the same category for the same user.

Different users can independently use the same custom category name.

## Layered Architecture

Controllers, services, repositories, entities, and DTOs are separated to keep responsibilities clear and make the application easier to maintain and test.

## User Data Isolation

Transactions, savings goals, and custom categories are associated with their owner.

Operations on user-owned resources verify ownership before returning, modifying, or deleting data.

Reports and goal-progress calculations also operate only on transactions belonging to the authenticated user.

## Default Categories

Default categories are shared application categories.

They cannot be modified or deleted by users.

## H2 Database

The assignment uses an in-memory H2 database.

Persistence across application restarts is therefore intentionally not provided.

---

# Repository

GitHub:

```text
https://github.com/Sattei/personal-finance-manager
```

Live API:

```text
https://personal-finance-manager-c8w1.onrender.com/api
```

---

# Author

Satyam Singh
