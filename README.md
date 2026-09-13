# ATM Simulator System

A secure **Java 17 Swing desktop ATM simulator** backed directly by MySQL.

> Development and modernization work is being done on the `fixed` branch. `main` remains unchanged.

## Features

- Polished, consistent desktop UI across the complete ATM journey
- Secure card-number + 4-digit PIN login
- PBKDF2 PIN hashing with a unique random salt
- Account blocking after 3 failed PIN attempts
- Account creation with optional initial deposit
- Deposit and cash withdrawal
- Fast cash presets
- Balance enquiry
- Mini statement with recent transactions
- PIN change
- Logout and shared dashboard navigation
- Live database connection status in the desktop UI
- HikariCP database connection pooling
- Prepared SQL statements throughout the DAO layer
- Atomic money operations with database transactions and row locking
- Maximum withdrawal: **Rs. 10,000 per transaction**
- Rolling 24-hour withdrawal limit: **Rs. 20,000**
- Unique transaction references
- Database credentials supplied through environment variables or JVM properties
- JUnit tests for security and transaction rules

## Architecture

```text
┌────────────────────────────────────────┐
│          Java Swing Desktop            │
│ Login → Signup → Dashboard → Operations│
│ Shared UI theme + authenticated shell  │
└───────────────────┬────────────────────┘
                    │ accountId
                    ▼
┌────────────────────────────────────────┐
│             Service Layer              │
│ AuthService • AccountService           │
│ TransactionService                     │
└───────────────────┬────────────────────┘
                    │
                    ▼
┌────────────────────────────────────────┐
│                DAO Layer               │
│ AccountDao • TransactionDao            │
└───────────────────┬────────────────────┘
                    │ pooled JDBC
                    ▼
┌────────────────────────────────────────┐
│       HikariCP Connection Pool         │
│      8 max • 2 minimum idle            │
└───────────────────┬────────────────────┘
                    │
                    ▼
┌────────────────────────────────────────┐
│                 MySQL                  │
│       accounts • transactions          │
└────────────────────────────────────────┘
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
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   ├── AccountService.java
│   │   │   └── TransactionService.java
│   │   └── ui/
│   │       ├── AtmFrame.java
│   │       └── AtmUi.java
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

The application uses HikariCP to reuse MySQL connections. DAO code still uses normal try-with-resources; closing a connection returns it to the pool instead of opening a new physical connection every time.

## Run the application

Build and test:

```bash
mvn clean verify
```

Run directly with Maven:

```bash
mvn exec:java -Dexec.mainClass=ASimulatorSystem.Main
```

On Windows PowerShell, quote the property argument if PowerShell parses it incorrectly:

```powershell
mvn exec:java "-Dexec.mainClass=ASimulatorSystem.Main"
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
8. Database connections are borrowed from HikariCP and returned automatically through try-with-resources.

PINs are never stored as plaintext and are never written to application logs.

## Development

Run the complete verification suite:

```bash
mvn clean verify
```

The GitHub Actions workflow runs the same Maven verification on pushes and pull requests.

## License

This project is intended as a learning and portfolio project for Java desktop application development, JDBC, SQL transactions, security, and software architecture.
