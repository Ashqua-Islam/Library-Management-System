package com.library;

import com.library.exception.BookNotAvailableException;
import com.library.exception.BookNotFoundException;
import com.library.exception.DuplicateUserException;
import com.library.exception.InvalidCredentialsException;
import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.model.User;
import com.library.service.AuthService;
import com.library.service.LibraryService;
import com.library.util.Logger;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final AuthService authService = new AuthService();
    private static final LibraryService libraryService = new LibraryService();

    public static void main(String[] args) {
        Logger.info("=== Smart Library Management System starting up ===");
        printBanner();

        boolean running = true;
        while (running) {
            System.out.println("\n===== WELCOME =====");
            System.out.println("1. Login");
            System.out.println("2. Register as Member");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleLogin();
                    break;
                case "2":
                    handleRegister();
                    break;
                case "3":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        Logger.info("=== Application shut down cleanly ===");
        System.out.println("Goodbye!");
    }

    private static void printBanner() {
        System.out.println("=========================================");
        System.out.println("   SMART LIBRARY MANAGEMENT SYSTEM");
        System.out.println("=========================================");
        System.out.println("Default admin login -> username: admin | password: admin123");
    }


    private static void handleRegister() {
        System.out.print("Choose a username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Choose a password: ");
        String password = scanner.nextLine().trim();
        System.out.print("Full name: ");
        String fullName = scanner.nextLine().trim();

        try {
            authService.register(username, password, fullName, false);
            System.out.println("Registration successful! You can now log in as a Member.");
        } catch (DuplicateUserException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            User user = authService.login(username, password);
            System.out.println("\nLogin successful. Welcome, " + user.getFullName()
                    + " (" + user.getRole() + ")");
            if ("ADMIN".equals(user.getRole())) {
                adminMenu(user);
            } else {
                memberMenu(user);
            }
        } catch (InvalidCredentialsException e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }


    private static void adminMenu(User admin) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n----- ADMIN MENU (" + admin.getUsername() + ") -----");
            System.out.println("1. Add Book");
            System.out.println("2. Update Book");
            System.out.println("3. Delete Book");
            System.out.println("4. View All Books");
            System.out.println("5. Search Books");
            System.out.println("6. View All Issue Records");
            System.out.println("7. View Overdue Books");
            System.out.println("8. Logout");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim()) {
                case "1": addBookFlow(); break;
                case "2": updateBookFlow(); break;
                case "3": deleteBookFlow(); break;
                case "4": viewAllBooks(); break;
                case "5": searchBooksFlow(); break;
                case "6": viewAllIssueRecords(); break;
                case "7": viewOverdueRecords(); break;
                case "8": loggedIn = false; break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void addBookFlow() {
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("Genre: ");
        String genre = scanner.nextLine().trim();
        int copies = readInt("Number of copies: ");

        Book book = libraryService.addBook(title, author, genre, copies);
        System.out.println("Book added successfully with ID: " + book.getBookId());
    }

    private static void updateBookFlow() {
        System.out.print("Enter Book ID to update: ");
        String id = scanner.nextLine().trim();
        try {
            System.out.print("New title (leave blank to keep unchanged): ");
            String title = scanner.nextLine().trim();
            System.out.print("New author (leave blank to keep unchanged): ");
            String author = scanner.nextLine().trim();
            System.out.print("New genre (leave blank to keep unchanged): ");
            String genre = scanner.nextLine().trim();
            int totalCopies = readInt("New total copies (-1 to keep unchanged): ");

            libraryService.updateBook(id, title, author, genre, totalCopies);
            System.out.println("Book updated successfully.");
        } catch (BookNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteBookFlow() {
        System.out.print("Enter Book ID to delete: ");
        String id = scanner.nextLine().trim();
        try {
            libraryService.deleteBook(id);
            System.out.println("Book deleted successfully.");
        } catch (BookNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewAllIssueRecords() {
        List<IssueRecord> records = libraryService.getAllIssueRecords();
        if (records.isEmpty()) {
            System.out.println("No issue records yet.");
            return;
        }
        records.forEach(r -> System.out.println(r.toString()));
    }

    private static void viewOverdueRecords() {
        List<IssueRecord> overdue = libraryService.getOverdueRecords();
        if (overdue.isEmpty()) {
            System.out.println("No overdue books. Nicely kept catalog!");
            return;
        }
        System.out.println("---- OVERDUE BOOKS ----");
        overdue.forEach(r -> System.out.println(r.toString()));
    }


    private static void memberMenu(User member) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n----- MEMBER MENU (" + member.getUsername() + ") -----");
            System.out.println("1. View All Books");
            System.out.println("2. Search Books");
            System.out.println("3. Issue a Book");
            System.out.println("4. Return a Book");
            System.out.println("5. My Active Issues");
            System.out.println("6. My Borrowing History");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim()) {
                case "1": viewAllBooks(); break;
                case "2": searchBooksFlow(); break;
                case "3": issueBookFlow(member); break;
                case "4": returnBookFlow(); break;
                case "5": viewActiveIssues(member); break;
                case "6": viewHistory(member); break;
                case "7": loggedIn = false; break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void issueBookFlow(User member) {
        System.out.print("Enter Book ID to issue: ");
        String bookId = scanner.nextLine().trim();
        try {
            IssueRecord record = libraryService.issueBook(bookId, member.getUsername());
            System.out.println("Book issued successfully! Issue ID: " + record.getIssueId()
                    + " | Due date: " + record.getDueDate());
        } catch (BookNotFoundException | BookNotAvailableException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void returnBookFlow() {
        System.out.print("Enter Issue ID to return: ");
        String issueId = scanner.nextLine().trim();
        try {
            IssueRecord record = libraryService.returnBook(issueId);
            double fine = record.calculateFine();
            System.out.println("Book returned successfully.");
            if (fine > 0) {
                System.out.printf("Late return fine due: %.2f%n", fine);
            } else {
                System.out.println("Returned on time, no fine.");
            }
        } catch (BookNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewActiveIssues(User member) {
        List<IssueRecord> active = libraryService.getActiveIssuesForUser(member.getUsername());
        if (active.isEmpty()) {
            System.out.println("You have no books currently issued.");
            return;
        }
        active.forEach(r -> System.out.println(r.toString()));
    }

    private static void viewHistory(User member) {
        List<IssueRecord> history = libraryService.getAllIssueHistoryForUser(member.getUsername());
        if (history.isEmpty()) {
            System.out.println("No borrowing history yet.");
            return;
        }
        history.forEach(r -> System.out.println(r.toString()));
    }


    private static void viewAllBooks() {
        List<Book> books = libraryService.getAllBooks();
        if (books.isEmpty()) {
            System.out.println("Catalog is empty.");
            return;
        }
        books.forEach(b -> System.out.println(b.toString()));
    }

    private static void searchBooksFlow() {
        System.out.print("Enter search keyword (title or author): ");
        String keyword = scanner.nextLine().trim();
        List<Book> results = libraryService.searchBooks(keyword);
        if (results.isEmpty()) {
            System.out.println("No matching books found.");
            return;
        }
        results.forEach(b -> System.out.println(b.toString()));
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }
}
