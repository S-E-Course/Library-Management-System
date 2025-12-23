# Library Management System

A **Java-based Library Management System** developed as part of the **Software Engineering course**.  
This project emphasizes **clean architecture**, **testability**, **code quality**, and **secure coding practices**, supported by **JUnit testing**, **JaCoCo coverage**, and **SonarCloud analysis**.

---

## Project Overview

The system manages a library through a **Command-Line Interface (CLI)** with multiple user roles:

- Admin
- Librarian
- User

Core features include:
- Media management (Books, CDs, Journals)
- User management
- Borrowing and returning items
- Fine calculation using strategy patterns
- Sending overdue reminders

The design follows a **layered architecture** to ensure maintainability and separation of concerns.

---

## Architecture

The project is organized into the following packages:

- `com.library.app` – Application entry points and CLI logic
- `com.library.service` – Business logic and services
- `com.library.dao` – Database access layer (DAO pattern)
- `com.library.model` – Domain models
- `com.library.strategy` – Fine calculation strategies
- `com.library.util` – Utility and helper classes

---

## Testing & Code Quality

### Unit Testing
- Implemented using **JUnit 5**
- **Mockito** is used to mock dependencies
- CLI workflows are tested by simulating user input

### Code Coverage
- Coverage measured using **JaCoCo**
- Branch coverage exceeds **80%**, as verified by **SonarCloud**. Here's a screenshot that shows the coverage of the project:

<img width="1183" height="230" alt="Screenshot 2025-12-23 232907" src="https://github.com/user-attachments/assets/eb913947-6fa6-406a-bdf6-5ecc0e572e7f" />

- Non-testable classes (such as the main entry point and database connection) are intentionally excluded from coverage metrics because they mainly contain CLI execution control logic.

### Static Code Analysis
- **SonarCloud** is used to analyze:
  - Code smells
  - Reliability issues
  - Security hotspots
- All reported issues were reviewed and resolved, including:
  - Reliability code smells
  - Branch coverage issues
  - SQL injection security hotspots

---

## Code Smells & Quality Improvements

During development, the project was analyzed using **SonarCloud**, which identified several code quality issues.  
All reported problems were reviewed, understood, and resolved. The improvements fall into three main categories:

### Maintainability Code Smells
Some classes were flagged for poor maintainability due to complex control flow and insufficient test coverage of decision branches.  
These issues were resolved by:
- Improving unit test coverage, especially for CLI control flows
- Ensuring all logical branches were executed and validated through tests
- Refactoring code where necessary to make behavior clearer and easier to maintain

As a result, the project achieved **high branch and line coverage**, improving long-term maintainability.

---

### Reliability Code Smells
SonarCloud detected reliability issues related to:
- Unhandled runtime exceptions
- Risk of unexpected application termination in CLI execution flows

These issues were fixed by:
- Adding proper exception handling in critical execution paths
- Ensuring that failures in service layers do not crash the application
- Validating user input and handling edge cases safely

This guarantees that the system behaves predictably even when errors occur.

---

### Security Code Smells (Security Hotspots)
Security hotspots were identified in the database access layer, particularly related to **dynamically constructed SQL queries**.

These issues were resolved by:
- Ensuring all database operations use safe parameter binding
- Reviewing dynamic SQL usage to confirm it does not expose the system to SQL injection risks
- Marking reviewed hotspots appropriately in SonarCloud after validation

This process ensured that the system follows secure coding practices and prevents common database security vulnerabilities.

---

## Quality Gate Result

After addressing all maintainability, reliability, and security issues:
- The project **passed the SonarCloud Quality Gate**
- Branch coverage exceeded the **80% requirement**
- All security hotspots were reviewed and resolved

---

## Security Considerations

- Database queries are handled safely using prepared statements
- All SonarCloud security hotspots were reviewed and addressed
- The project follows secure coding best practices for database interaction

---

## Technologies Used

- Java (JDK 17)
- Maven
- JUnit 5
- Mockito
- JaCoCo
- SonarCloud
- Eclipse IDE
- Git & GitHub

---

## How to Run

1. Clone the repository
2. Import the project into Eclipse
3. Ensure JDK 17 is configured
4. Run the application using the `Main` class
