# Smart Library Management System

A console-based Java application that lets a library run its day-to-day
operations: managing the book catalog, registering members, issuing and
returning books, and automatically calculating late-return fines.

Built as the "Build Your Own Project" submission for the **Programming in
Java** course.

---

## Overview

Real libraries need to track three things at once: *who* is allowed to
borrow, *what* books exist and how many copies are free, and *which* books
are currently out and whether they're overdue. This project models all
three as a small, testable Java application with no external dependencies
just the JDK standard library so it can be compiled and run anywhere.

Data is persisted to plain text files under `data/`, so nothing is lost
between runs, and every significant action is written to `logs/app.log`
for traceability.

## Features

- **User Management** — Register as a Member or log in as Admin/Member.
  Passwords are hashed with SHA-256 before ever touching disk.
- **Book Inventory Management (CRUD)** — Admins can add, update, delete,
  list, and search books by title or author.
- **Issue / Return & Fine Management** — Members can issue an available
  copy, return it, and see any late fine (₹5/day after a 14-day loan
  period), calculated automatically from the due date.
- **Reporting** — Admins can view the full issue history and a live list
  of overdue books; members can view their own active issues and
  borrowing history.
- **Persistence & Logging** — All data survives a restart (flat-file
  storage); every action is timestamped and logged.

## Technologies / Tools Used

| Category         | Choice                                   |
|-------------------|-------------------------------------------|
| Language           | Java 21 (standard library only)          |
| Build              | `javac` (no external build tool required) |
| Persistence        | Plain text files (`data/*.txt`)          |
| Security           | `java.security.MessageDigest` (SHA-256)  |
| Version control    | Git / GitHub                             |

## Project Structure

```
LibraryManagementSystem/
├── src/main/java/com/library/
│   ├── Main.java                    # Console UI / entry point
│   ├── model/
│   │   ├── User.java                # abstract base
│   │   ├── Admin.java
│   │   ├── Member.java
│   │   ├── Book.java
│   │   └── IssueRecord.java
│   ├── service/
│   │   ├── AuthService.java         # Module 1: User Management
│   │   └── LibraryService.java      # Modules 2 & 3: Books + Issue/Return
│   ├── util/
│   │   ├── FileStorage.java
│   │   ├── Logger.java
│   │   └── PasswordUtil.java
│   └── exception/
│       ├── BookNotFoundException.java
│       ├── BookNotAvailableException.java
│       ├── InvalidCredentialsException.java
│       └── DuplicateUserException.java
├── diagrams/                        # architecture, workflow, UML diagrams
├── data/                            # created automatically at runtime
├── logs/                            # created automatically at runtime
├── statement.md
└── README.md
```

## Steps to Install & Run

**Prerequisite:** JDK 17 or newer installed (`java -version` to check).

```bash
# 1. Clone the repository
git clone https://github.com/<Ashqua-Islam>/LibraryManagementSystem.git
cd LibraryManagementSystem

# 2. Compile
find src -name "*.java" > sources.txt
javac -d out @sources.txt

# 3. Run
java -cp out com.library.Main
```

On first run the app seeds a default admin account:

```
username: admin
password: admin123
```

Use it to log in and start adding books, or choose "Register as Member"
from the welcome menu to create a member account.

## Instructions for Testing

Manual test flow (no external test framework needed to exercise it):

1. Start the app, log in as `admin` / `admin123`.
2. Add 2–3 books via **Add Book**.
3. Log out, register a new member account, log back in as that member.
4. Issue one of the books you just added — note the Issue ID shown.
5. Use **My Active Issues** to confirm it shows up with a due date.
6. Return it using **Return a Book** and the Issue ID — confirm the fine
   calculation (0 if returned same day).
7. Log back in as `admin` and check **View All Issue Records** and
   **View Overdue Books** to confirm the transaction is reflected.
8. Inspect `data/*.txt` and `logs/app.log` to confirm persistence and
   logging are working.

## Screenshots

See the `diagrams/` folder for the architecture, workflow, use case,
class, sequence, and data-schema diagrams referenced in the project
report.
