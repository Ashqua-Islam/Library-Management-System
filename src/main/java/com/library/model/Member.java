package com.library.model;


public class Member extends User {

    private static final long serialVersionUID = 1L;

    public Member(String username, String passwordHash, String fullName) {
        super(username, passwordHash, fullName);
    }

    @Override
    public String getRole() {
        return "MEMBER";
    }
}
