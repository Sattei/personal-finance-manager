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

## Features

### Authentication

- User registration
- User login
- Session-based authentication
- Secure password storage using BCrypt
- Logout and session invalidation
- Protected API endpoints
- User data isolation

### Transactions

Users can:

- Create income and expense transactions
- View transactions
- Filter transactions
- Update transactions
- Delete transactions

Transaction validation includes:

- Positive transaction amount
- Valid transaction date
- No future-dated transactions
- Valid and accessible category
- Immutable transaction date during updates

### Categories

The application provides the following default categories:

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

A custom category that is currently referenced by a transaction cannot be deleted.

### Savings Goals

Users can:

- Create savings goals
- View savings goals
- Update target amount
- Update target date
- Delete savings goals

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

The project follows a layered Spring Boot architecture:

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

Handles HTTP requests and responses.

```text
controller/
```

### Service Layer

Contains business logic, validation, ownership checks, calculations, and application rules.

```text
service/
```

### Repository Layer

Uses Spring Data JPA to communicate with the database.

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

Application exceptions are handled globally using Spring's `@ControllerAdvice`.

The API uses appropriate HTTP status codes including:

- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`
- `404 Not Found`
- `409 Conflict`

---

## API Endpoints

Base path:

```text
/api
```

### Authentication

#### Register

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

#### Login

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

#### Logout

```http
POST /api/auth/logout
```

Example response:

```json
{
  "message": "Logout successful"
}
```

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

Example:

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

### Delete Category

```http
DELETE /api/categories/{name}
```

Only unused custom categories belonging to the authenticated user can be deleted.

---

## Transactions

### Create Transaction

```http
POST /api/transactions
```

Example:

```json
{
  "amount": 5000.0,
  "date": "2024-01-15",
  "category": "Salary",
  "description": "January Salary"
}
```

### Get Transactions

```http
GET /api/transactions
```

Supported optional filters include:

```text
startDate
endDate
categoryId
category
type
```

Example:

```http
GET /api/transactions?startDate=2024-01-01&endDate=2024-01-31
```

Example category filter:

```http
GET /api/transactions?category=Salary
```

Transactions are returned newest first.

### Update Transaction

```http
PUT /api/transactions/{id}
```

Example:

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

Example:

```json
{
  "goalName": "Emergency Fund",
  "targetAmount": 10000.0,
  "targetDate": "2027-12-31",
  "startDate": "2026-01-01"
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
GET /api/reports/monthly/2024/1
```

Example response:

```json
{
  "month": 1,
  "year": 2024,
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
GET /api/reports/yearly/2024
```

---

## Running the Application Locally

### Prerequisites

Install:

- Java 17
- Git

The project includes the Maven Wrapper, so a separate Maven installation is not required.

Verify Java:

```bash
java -version
```

The project should run with Java 17.

### Clone the Repository

```bash
git clone <repository-url>
cd personal-finance-manager
```

### Run Tests

Windows:

```powershell
.\mvnw.cmd clean test
```

Linux/macOS:

```bash
./mvnw clean test
```

### Start the Application

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

The API runs locally at:

```text
http://localhost:8080
```

Base API URL:

```text
http://localhost:8080/api
```

---

## Database

The application uses an in-memory H2 database.

```text
jdbc:h2:mem:financedb
```

The database is recreated when the application restarts.

The H2 browser console is disabled in the production-ready configuration.

---

## Security

The application uses Spring Security with session-based authentication.

Security features include:

- BCrypt password hashing
- HTTP session authentication
- HttpOnly session cookies
- Secure cookies in production
- Protected application endpoints
- User-level resource ownership validation
- Session invalidation on logout

Only the following endpoints are publicly accessible:

```text
POST /api/auth/register
POST /api/auth/login
```

All other application endpoints require authentication.

For HTTPS deployments, the following environment variable is used:

```text
SESSION_COOKIE_SECURE=true
```

---

## Testing

The project contains unit tests using:

- JUnit 5
- Mockito
- Spring Boot Test

Run the complete unit-test suite with:

```bash
./mvnw clean test
```

The project currently passes all local unit tests.

```text
Tests: 65
Failures: 0
Errors: 0
```

### End-to-End Tests

The supplied assessment E2E script can be executed against a running API:

```bash
bash financial_manager_tests.sh http://localhost:8080/api
```

Local validation result:

```text
Total Tests Executed: 86
Tests Passed: 86
Tests Failed: 0
Success Rate: 100%
```

---

## Code Coverage

JaCoCo is used for code coverage.

Generate the coverage report using:

```bash
./mvnw clean test
```

The report is generated under:

```text
target/site/jacoco/index.html
```

The project achieves coverage above the required 80% threshold.

---

## JavaDoc

Public application classes and methods are documented using JavaDoc.

Generate the documentation with:

```bash
./mvnw javadoc:javadoc
```

Generated documentation is available under:

```text
target/site/apidocs/index.html
```

---

## Production Configuration

The server port is configured using:

```properties
server.port=${PORT:8080}
```

This allows cloud platforms such as Render to provide the application's runtime port automatically.

Production session cookies can be secured using:

```text
SESSION_COOKIE_SECURE=true
```

---

## Deployment

The application can be deployed as a standard Spring Boot application.

Build the JAR:

```bash
./mvnw clean package
```

The generated application JAR is placed inside:

```text
target/
```

For deployment platforms such as Render, the application can be started with:

```bash
java -jar target/personal-finance-manager-0.0.1-SNAPSHOT.jar
```

After deployment, the assessment E2E suite can be executed against the deployed API:

```bash
bash financial_manager_tests.sh https://YOUR-DEPLOYED-URL/api
```

---

## Design Decisions

### Session Authentication

Session-based authentication was used instead of JWT because the application requires authenticated server-side sessions and the assignment specifies session-based authentication.

### BigDecimal for Monetary Values

`BigDecimal` is used for financial values to avoid floating-point precision issues.

### Layered Architecture

Controllers, services, repositories, entities, and DTOs are separated to keep responsibilities clear and make the application easier to test and maintain.

### User Data Isolation

Transactions, goals, and custom categories are associated with their owner.

Operations on user-owned resources verify ownership before modification or deletion.

### Default Categories

Default categories are shared application categories and cannot be modified or deleted by users.

---

## Author

Satyam Singh
