package com.library.model;

import java.io.Serializable;
import java.time.LocalDate;

public class IssueRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int LOAN_PERIOD_DAYS = 14;
    private static final double FINE_PER_DAY = 5.0; // currency units per overdue day

    private final String issueId;
    private final String bookId;
    private final String username;
    private final LocalDate issueDate;
    private final LocalDate dueDate;
    private LocalDate returnDate; // null while the book is still out

    public IssueRecord(String issueId, String bookId, String username, LocalDate issueDate) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.username = username;
        this.issueDate = issueDate;
        this.dueDate = issueDate.plusDays(LOAN_PERIOD_DAYS);
        this.returnDate = null;
    }

    public IssueRecord(String issueId, String bookId, String username,
                        LocalDate issueDate, LocalDate dueDate, LocalDate returnDate) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.username = username;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public String getIssueId() {
        return issueId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getUsername() {
        return username;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returnDate != null;
    }

    public void markReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Calculates the fine owed. If the book has been returned the fine
     * is fixed based on the actual return date; if it is still out, the
     * fine is projected as of today so members can see what they owe so far.
     */
    public double calculateFine() {
        LocalDate compareDate = (returnDate != null) ? returnDate : LocalDate.now();
        long overdueDays = dueDate.isBefore(compareDate)
                ? java.time.temporal.ChronoUnit.DAYS.between(dueDate, compareDate)
                : 0;
        return overdueDays * FINE_PER_DAY;
    }

    public String toRecord() {
        return String.join("|",
                issueId, bookId, username,
                issueDate.toString(), dueDate.toString(),
                returnDate == null ? "NULL" : returnDate.toString());
    }

    @Override
    public String toString() {
        String status = isReturned() ? "Returned on " + returnDate : "Due " + dueDate;
        double fine = calculateFine();
        String fineStr = fine > 0 ? String.format(" | Fine: %.2f", fine) : "";
        return String.format("Issue#%s | Book:%s | User:%s | Issued:%s | %s%s",
                issueId, bookId, username, issueDate, status, fineStr);
    }
}
