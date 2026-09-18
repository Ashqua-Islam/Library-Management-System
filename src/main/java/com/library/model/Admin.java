package com.library.model;


public class Admin extends User {

    private static final long serialVersionUID = 1L;

    public Admin(String username, String passwordHash, String fullName) {
        super(username, passwordHash, fullName);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }
}
