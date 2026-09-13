# ATM Simulator System

A secure **Java 17 Swing desktop ATM simulator** backed directly by MySQL.

> Development and modernization work is being done on the `fixed` branch. `main` remains unchanged.

## Features

- Secure card-number + 4-digit PIN login
- PBKDF2 PIN hashing with a unique random salt
- Account blocking after 3 failed PIN attempts
- Account creation with optional initial deposit
- Deposit and cash withdrawal
- Fast cash presets
- Balance enquiry
- Mini statement with recent transactions
- PIN change
- Logout and clean desktop navigation
- Prepared SQL statements throughout the DAO layer
- Atomic money operations with database transactions and row locking
- Maximum withdrawal: **Rs. 10,000 per transaction**
- Rolling 24-hour withdrawal limit: **Rs. 20,000**
- Unique transaction references
- Database credentials supplied through environment variables or JVM properties
- JUnit tests for security and transaction rules

## Architecture

```text
┌───────────────────────────────┐
│       Java Swing Desktop      │
│ Login • Signup • ATM Menu     │
│ Deposit • Withdraw • Balance  │
│ Statement • Fast Cash • PIN   │
└───────────────┬───────────────┘
                │
                ▼
┌───────────────────────────────┐
│          Service Layer        │
│ AuthService                   │
│ AccountService                │
│ TransactionService            │
└───────────────┬───────────────┘
                │
                ▼
┌───────────────────────────────┐
│             DAO               │
│ AccountDao • TransactionDao   │
└───────────────┬───────────────┘
                │ JDBC
                ▼
┌───────────────────────────────┐
│             MySQL             │
│ accounts • transactions       │
└───────────────────────────────┘
```

The application is intentionally **desktop-only**. There is no REST server, JWT session, browser frontend, or cloud runtime required by the ATM application.

## Project structure

```text
ATM-Simulator-System/
├── database/
│   └── schema.sql
├── src/
│   ├── main/java/ASimulatorSystem/
│   │   ├── Main.java
│   │   ├── Login.java
│   │   ├── Signup.java
│   │   ├── Transactions.java
│   │   ├── Deposit.java
│   │   ├── Withdrawal.java
│   │   ├── FastCash.java
│   │   ├── BalanceEnquiry.java
│   │   ├── MiniStatement.java
│   │   ├── Pin.java
│   │   ├── config/
│   │   │   └── DatabaseConfig.java
│   │   ├── dao/
│   │   │   ├── AccountDao.java
│   │   │   └── TransactionDao.java
│   │   ├── security/
│   │   │   └── PinHasher.java
│   │   └── service/
│   │       ├── AuthService.java
│   │       ├── AccountService.java
│   │       └── TransactionService.java
│   └── test/java/
├── pom.xml
└── .github/workflows/ci.yml
```

## Requirements

- Java 17 or newer
- Maven 3.9+
- MySQL 8+

## Database setup

Create the database and tables using:

```bash
mysql -u root -p < database/schema.sql
```

The schema creates:

- `accounts` — card number, salted PIN hash, balance, account status and failed-login count
- `transactions` — immutable deposit/withdrawal records and balance-after snapshots

The current schema is intended for a fresh installation. It replaces the original project's `login`/`bank` table layout.

## Database configuration

The desktop application reads these values from JVM system properties first, then environment variables:

```text
DB_URL=jdbc:mysql://localhost:3306/bankmanagementsystem
DB_USERNAME=root
DB_PASSWORD=your-password
```

Examples:

**Windows PowerShell**

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/bankmanagementsystem"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your-password"
```

**JVM properties**

```bash
mvn -Ddb.url="jdbc:mysql://localhost:3306/bankmanagementsystem" -Ddb.username=root -Ddb.password=your-password clean package
```

Do not commit real database credentials.

## Run the application

Build and test:

```bash
mvn clean verify
```

Run directly with Maven:

```bash
mvn exec:java -Dexec.mainClass=ASimulatorSystem.Main
```

Or build the executable JAR:

```bash
mvn clean package
java -jar target/atm-simulator-system-4.0.0.jar
```

The application opens the Swing login window.

## Security model

1. The user enters a card number and PIN in the desktop login screen.
2. `AuthService` loads the account by card number.
3. `PinHasher` verifies the PIN against the stored PBKDF2 hash.
4. Three consecutive failed attempts block the account.
5. A successful login creates a session using the account ID; the PIN is not passed between screens.
6. Transaction services perform validation before calling the DAO layer.
7. Withdrawals lock the account row and update the balance and transaction history atomically.

PINs are never stored as plaintext and are never written to application logs.

## Development

Run the complete verification suite:

```bash
mvn clean verify
```

The GitHub Actions workflow runs the same Maven verification on pushes and pull requests.

## License

This project is intended as a learning and portfolio project for Java desktop application development, JDBC, SQL transactions, security, and software architecture.
