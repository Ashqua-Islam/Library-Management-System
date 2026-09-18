package com.library.model;

import java.io.Serializable;

public class Book implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String bookId;
    private String title;
    private String author;
    private String genre;
    private int totalCopies;
    private int availableCopies;

    public Book(String bookId, String title, String author, String genre, int totalCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    /**
     * Adjusts the total stock of this title and keeps available copies
     * in sync (used when the admin updates the catalog).
     */
    public void setTotalCopies(int totalCopies) {
        int issuedOut = this.totalCopies - this.availableCopies;
        this.totalCopies = totalCopies;
        this.availableCopies = Math.max(0, totalCopies - issuedOut);
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public void decrementAvailable() {
        if (availableCopies > 0) {
            availableCopies--;
        }
    }

    public void incrementAvailable() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    public String toRecord() {
        return String.join("|", bookId, title, author, genre,
                String.valueOf(totalCopies), String.valueOf(availableCopies));
    }

    @Override
    public String toString() {
        return String.format("[%s] %-30s by %-20s | Genre: %-12s | Available: %d/%d",
                bookId, title, author, genre, availableCopies, totalCopies);
    }
}
