# Project Statement

## Problem Statement

Small and mid-sized libraries frequently rely on paper registers or
disconnected spreadsheets to track book inventory, membership, and due
dates. This creates three recurring problems: books go missing without a
clear record of who has them, overdue fines are calculated inconsistently
(or not at all), and staff cannot quickly answer "is this title
available right now?" The **Smart Library Management System** solves
this by giving a librarian and their members a single, consistent
console application to manage the entire lending lifecycle — from
cataloging a new title to charging the correct fine when it comes back
late.

## Scope of the Project

The project is a self-contained, offline Java console application. In
scope:

- Registering and authenticating two kinds of users: Admin and Member.
- Full CRUD management of the book catalog by an Admin.
- Searching/browsing the catalog by any user.
- Issuing and returning books, with automatic due-date and fine
  calculation (14-day loan period, ₹5/day late fee).
- Reporting: overdue books, full issue history, and per-member
  borrowing history.
- Persisting all data to local files so the system's state survives
  a restart, and logging every significant action for traceability.

Out of scope (explicitly not attempted, to keep the project focused):
a graphical user interface, a networked/multi-user server, integration
with a real relational database, and email/SMS notifications for due
dates — all reasonable future enhancements but not required to
demonstrate the core Java concepts this course covers.

## Target Users

- **Library Admin / Librarian** — manages the catalog and monitors
  overdue books and overall lending activity.
- **Library Member** — searches the catalog, borrows books, returns
  them, and keeps track of their own fines and history.

## High-Level Features

1. **User Management Module** — registration, login, SHA-256 password
   hashing, role-based menu (Admin vs Member).
2. **Book Inventory Management Module** — add, update, delete, list,
   and search books; tracks total vs. available copies per title.
3. **Issue / Return & Fine Management Module** — issue a copy to a
   member, return it, calculate late fines automatically, and generate
   overdue/history reports.

Together these three modules cover the full lifecycle of a library
transaction, which is what the course project brief asks for: a
meaningful problem, a designed solution, an implementation using the
tools learned in the course (OOP, collections, exceptions, file I/O),
and documentation to demonstrate understanding.
