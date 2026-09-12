# ATM Simulator System

A secure ATM simulator built with **Java 17**, **Java Swing**, **Spring Boot**, **Spring Data JPA**, **MySQL**, and **JWT authentication**.

> All modernization work is being developed on the `fixed` branch. `main` is intentionally left unchanged.

## Architecture

```text
                         ┌─────────────────────┐
                         │     Java Swing UI   │
                         │  Login / ATM screens│
                         └──────────┬──────────┘
                                    │
                         Service / DAO layer
                                    │
                                    ▼
                              ┌──────────┐
                              │  MySQL   │
                              └──────────┘

React / Next.js / other clients
             │
             ▼
     ┌───────────────────┐
     │ Spring Boot REST  │
     │ /api/v1/...       │
     └─────────┬─────────┘
               │
       JWT + service layer
               │
               ▼
             MySQL
```

## Security improvements

- PINs are hashed with BCrypt; plaintext PINs are never stored.
- Login uses parameterized SQL in the Swing DAO.
- Three failed PIN attempts block an account.
- JWT bearer authentication protects REST ATM endpoints.
- Withdrawals use database row locking and a transaction so balance updates are atomic.
- Maximum withdrawal: **Rs. 10,000 per transaction**.
- Rolling 24-hour withdrawal limit: **Rs. 20,000**.
- Every deposit/withdrawal receives a unique transaction reference.
- Secrets and database credentials are supplied through environment variables.
- CORS origins are configurable.

## Database

Run `database/schema.sql` against MySQL 8+.

The schema contains:

- `accounts` — card number, hashed PIN, balance, status, failed attempts and timestamps.
- `transactions` — immutable deposit/withdrawal history with balance-after snapshots.

## Run with Maven

Requirements: Java 17+, Maven 3.9+, MySQL 8+.

```bash
mvn clean verify
mvn spring-boot:run
```

Set these environment variables when needed:

```text
DB_URL=jdbc:mysql://localhost:3306/bankmanagementsystem?useSSL=false&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=your-password
JWT_SECRET=your-long-random-secret
JWT_EXPIRATION_MS=3600000
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
```

## Run with Docker

```bash
cp .env.example .env
# edit .env and set strong values
docker compose up --build
```

API: `http://localhost:8080`

Health: `http://localhost:8080/actuator/health`

Swagger UI: `http://localhost:8080/swagger-ui.html`

OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## REST API

### Authentication

`POST /api/v1/auth/signup`

```json
{
  "cardNumber": "123456789012",
  "pin": "1234",
  "initialDeposit": 1000.00
}
```

`POST /api/v1/auth/login`

```json
{
  "cardNumber": "123456789012",
  "pin": "1234"
}
```

The response contains a JWT. Send it on protected requests:

```text
Authorization: Bearer <token>
```

### ATM operations

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/v1/atm/balance` | Current balance |
| POST | `/api/v1/atm/deposit` | Deposit money |
| POST | `/api/v1/atm/withdraw` | Withdraw money |
| GET | `/api/v1/atm/transactions` | Last 10 transactions |
| POST | `/api/v1/atm/pin` | Change PIN |

## Project structure

```text
ATM-Simulator-System/
├── database/
│   └── schema.sql
├── src/
│   └── ASimulatorSystem/
│       ├── backend/              # Spring Boot REST API
│       │   ├── config/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── entity/
│       │   ├── repository/
│       │   ├── security/
│       │   └── service/
│       ├── config/               # Swing database configuration
│       ├── dao/                  # Swing persistence layer
│       ├── security/             # Shared PIN hashing
│       └── service/              # Swing business layer
├── src/main/resources/
│   └── application.properties
├── src/test/java/                # Automated tests
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── .github/workflows/ci.yml
```

## Development notes

The Swing client and REST API currently share the same database schema while keeping their persistence implementations separate. This makes the project usable as a desktop application now and ready for a web/mobile client later.

For production deployment, use a managed MySQL instance, a strong randomly generated `JWT_SECRET`, HTTPS, restricted CORS origins, and non-root database credentials.
