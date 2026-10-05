# ATM Simulator System

A secure **Java 17 Swing desktop ATM simulator** with zero-configuration standalone local storage.

## Features

- Polished, consistent desktop UI across the complete ATM journey
- Secure card-number + 4-digit PIN login
- Pre-seeded demo account ready out of the box
- PBKDF2 PIN hashing with a unique random salt
- Account blocking after 3 failed PIN attempts
- Account creation with optional initial deposit
- Deposit and cash withdrawal
- Fast cash presets
- Balance enquiry
- Mini statement with recent transactions
- PIN change
- Logout and shared dashboard navigation
- Zero database setup required: embedded local storage with automatic file persistence
- Maximum withdrawal: **Rs. 10,000 per transaction**
- Rolling 24-hour withdrawal limit: **Rs. 20,000**
- Unique transaction references
- JUnit tests for security, transactions, and authentication

## Quick Start / Demo Account

The application comes pre-configured with a demo account:
- **Card Number:** `1234567890123456`
- **PIN:** `1234`
- **Initial Balance:** `Rs. 10,000.00`

You can also click **"CREATE NEW ACCOUNT"** on the login screen to register your own account.

## Requirements

- Java 17 or newer
- Maven 3.9+

## Run the application

Build and test:

```bash
mvn clean test
```

Run directly with Maven:

```powershell
mvn exec:java
```

Or build the executable JAR:

```powershell
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
