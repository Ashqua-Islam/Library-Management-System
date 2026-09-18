package com.library.model;

import java.io.Serializable;

public abstract class User implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String username;
    protected String passwordHash;
    protected String fullName;

    public User(String username, String passwordHash, String fullName) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    /**
     * Every concrete subtype must state its role so the menu layer
     * can decide which options to present after login.
     */
    public abstract String getRole();

    /**
     * Serializes this user to a single pipe-delimited line for
     * file-based persistence (see FileStorage).
     */
    public String toRecord() {
        return String.join("|", getRole(), username, passwordHash, fullName);
    }
}
