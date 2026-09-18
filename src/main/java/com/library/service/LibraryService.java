package com.library.service;

import com.library.exception.BookNotAvailableException;
import com.library.exception.BookNotFoundException;
import com.library.model.Book;
import com.library.model.IssueRecord;
import com.library.util.FileStorage;
import com.library.util.Logger;

import java.time.LocalDate;
import java.util.*;

public class LibraryService {

    private static final String BOOKS_FILE = "data/books.txt";
    private static final String ISSUES_FILE = "data/issues.txt";

    private final Map<String, Book> booksById = new HashMap<>();
    private final List<IssueRecord> issueRecords = new ArrayList<>();

    private int nextBookSeq = 1;
    private int nextIssueSeq = 1;

    public LibraryService() {
        loadBooks();
        loadIssues();
    }

    // ---------------------------------------------------------------
    // Module 2: Book Inventory Management (Create, Read, Update, Delete)
    // ---------------------------------------------------------------

    public Book addBook(String title, String author, String genre, int copies) {
        String bookId = "B" + String.format("%04d", nextBookSeq++);
        Book book = new Book(bookId, title, author, genre, copies);
        booksById.put(bookId, book);
        persistBooks();
        Logger.info("Book added: " + bookId + " - " + title);
        return book;
    }

    public Book findBookById(String bookId) throws BookNotFoundException {
        Book book = booksById.get(bookId);
        if (book == null) {
            throw new BookNotFoundException("No book found with ID: " + bookId);
        }
        return book;
    }

    public void updateBook(String bookId, String title, String author, String genre, int totalCopies)
            throws BookNotFoundException {
        Book book = findBookById(bookId);
        if (title != null && !title.isBlank()) book.setTitle(title);
        if (author != null && !author.isBlank()) book.setAuthor(author);
        if (genre != null && !genre.isBlank()) book.setGenre(genre);
        if (totalCopies >= 0) book.setTotalCopies(totalCopies);
        persistBooks();
        Logger.info("Book updated: " + bookId);
    }

    public void deleteBook(String bookId) throws BookNotFoundException {
        findBookById(bookId); // validates existence, throws if missing
        booksById.remove(bookId);
        persistBooks();
        Logger.info("Book deleted: " + bookId);
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>(booksById.values());
        books.sort(Comparator.comparing(Book::getBookId));
        return books;
    }

    /** Simple case-insensitive substring search across title and author. */
    public List<Book> searchBooks(String keyword) {
        String needle = keyword.toLowerCase();
        List<Book> results = new ArrayList<>();
        for (Book book : booksById.values()) {
            if (book.getTitle().toLowerCase().contains(needle)
                    || book.getAuthor().toLowerCase().contains(needle)) {
                results.add(book);
            }
        }
        results.sort(Comparator.comparing(Book::getBookId));
        return results;
    }

    // ---------------------------------------------------------------
    // Module 3: Issue / Return & Fine Management
    // ---------------------------------------------------------------

    public IssueRecord issueBook(String bookId, String username)
            throws BookNotFoundException, BookNotAvailableException {
        Book book = findBookById(bookId);
        if (!book.isAvailable()) {
            throw new BookNotAvailableException(
                    "Book '" + book.getTitle() + "' has no copies available right now.");
        }
        book.decrementAvailable();
        String issueId = "I" + String.format("%04d", nextIssueSeq++);
        IssueRecord record = new IssueRecord(issueId, bookId, username, LocalDate.now());
        issueRecords.add(record);

        persistBooks();
        persistIssues();
        Logger.info("Book issued: " + bookId + " to " + username + " (due " + record.getDueDate() + ")");
        return record;
    }

    public IssueRecord returnBook(String issueId) throws BookNotFoundException {
        IssueRecord record = issueRecords.stream()
                .filter(r -> r.getIssueId().equals(issueId) && !r.isReturned())
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException(
                        "No active issue record found with ID: " + issueId));

        record.markReturned(LocalDate.now());
        Book book = booksById.get(record.getBookId());
        if (book != null) {
            book.incrementAvailable();
        }
        double fine = record.calculateFine();

        persistBooks();
        persistIssues();
        Logger.info("Book returned: issue " + issueId + " | fine charged: " + fine);
        return record;
    }

    public List<IssueRecord> getActiveIssuesForUser(String username) {
        List<IssueRecord> result = new ArrayList<>();
        for (IssueRecord r : issueRecords) {
            if (r.getUsername().equals(username) && !r.isReturned()) {
                result.add(r);
            }
        }
        return result;
    }

    public List<IssueRecord> getAllIssueHistoryForUser(String username) {
        List<IssueRecord> result = new ArrayList<>();
        for (IssueRecord r : issueRecords) {
            if (r.getUsername().equals(username)) {
                result.add(r);
            }
        }
        return result;
    }

    /** Full activity report for the admin dashboard. */
    public List<IssueRecord> getAllIssueRecords() {
        return new ArrayList<>(issueRecords);
    }

    public List<IssueRecord> getOverdueRecords() {
        List<IssueRecord> overdue = new ArrayList<>();
        for (IssueRecord r : issueRecords) {
            if (!r.isReturned() && r.getDueDate().isBefore(LocalDate.now())) {
                overdue.add(r);
            }
        }
        return overdue;
    }

    // ---------------------------------------------------------------
    // Persistence helpers
    // ---------------------------------------------------------------

    private void loadBooks() {
        List<String> lines = FileStorage.readLines(BOOKS_FILE);
        for (String line : lines) {
            String[] p = line.split("\\|", -1);
            if (p.length != 6) continue;
            Book book = new Book(p[0], p[1], p[2], p[3], Integer.parseInt(p[4]));
            // restore available copies precisely (constructor sets it equal to total)
            int available = Integer.parseInt(p[5]);
            while (book.getAvailableCopies() > available) {
                book.decrementAvailable();
            }
            booksById.put(book.getBookId(), book);
            int seq = extractSeq(book.getBookId());
            if (seq >= nextBookSeq) nextBookSeq = seq + 1;
        }
        Logger.info("Loaded " + booksById.size() + " book title(s) from disk.");
    }

    private void loadIssues() {
        List<String> lines = FileStorage.readLines(ISSUES_FILE);
        for (String line : lines) {
            String[] p = line.split("\\|", -1);
            if (p.length != 6) continue;
            LocalDate issueDate = LocalDate.parse(p[3]);
            LocalDate dueDate = LocalDate.parse(p[4]);
            LocalDate returnDate = p[5].equals("NULL") ? null : LocalDate.parse(p[5]);
            IssueRecord record = new IssueRecord(p[0], p[1], p[2], issueDate, dueDate, returnDate);
            issueRecords.add(record);
            int seq = extractSeq(p[0]);
            if (seq >= nextIssueSeq) nextIssueSeq = seq + 1;
        }
        Logger.info("Loaded " + issueRecords.size() + " issue record(s) from disk.");
    }

    private int extractSeq(String id) {
        try {
            return Integer.parseInt(id.substring(1));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void persistBooks() {
        List<String> records = new ArrayList<>();
        for (Book b : getAllBooks()) {
            records.add(b.toRecord());
        }
        FileStorage.writeLines(BOOKS_FILE, records);
    }

    private void persistIssues() {
        List<String> records = new ArrayList<>();
        for (IssueRecord r : issueRecords) {
            records.add(r.toRecord());
        }
        FileStorage.writeLines(ISSUES_FILE, records);
    }
}
