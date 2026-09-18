package com.library.service;

import com.library.exception.DuplicateUserException;
import com.library.exception.InvalidCredentialsException;
import com.library.model.Admin;
import com.library.model.Member;
import com.library.model.User;
import com.library.util.FileStorage;
import com.library.util.Logger;
import com.library.util.PasswordUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthService {

    private static final String USERS_FILE = "data/users.txt";

    private final Map<String, User> usersByUsername = new HashMap<>();

    public AuthService() {
        loadUsers();
        seedDefaultAdminIfEmpty();
    }

    private void loadUsers() {
        List<String> lines = FileStorage.readLines(USERS_FILE);
        for (String line : lines) {
            String[] parts = line.split("\\|", -1);
            if (parts.length != 4) {
                continue; // skip malformed lines rather than crash (reliability)
            }
            String role = parts[0];
            String username = parts[1];
            String passwordHash = parts[2];
            String fullName = parts[3];

            User user = role.equals("ADMIN")
                    ? new Admin(username, passwordHash, fullName)
                    : new Member(username, passwordHash, fullName);
            usersByUsername.put(username, user);
        }
        Logger.info("Loaded " + usersByUsername.size() + " user account(s) from disk.");
    }

    /**
     * Guarantees the system is usable out of the box: if no accounts
     * exist yet, create a default admin so the very first run does not
     * dead-end at an empty login screen.
     */
    private void seedDefaultAdminIfEmpty() {
        if (usersByUsername.isEmpty()) {
            Admin defaultAdmin = new Admin("admin", PasswordUtil.hash("admin123"), "Default Administrator");
            usersByUsername.put(defaultAdmin.getUsername(), defaultAdmin);
            persistAll();
            Logger.info("No users found. Seeded default admin account (username: admin / password: admin123).");
        }
    }

    public User register(String username, String plainPassword, String fullName, boolean isAdmin)
            throws DuplicateUserException {
        if (usersByUsername.containsKey(username)) {
            throw new DuplicateUserException("Username '" + username + "' is already taken.");
        }
        String hash = PasswordUtil.hash(plainPassword);
        User newUser = isAdmin
                ? new Admin(username, hash, fullName)
                : new Member(username, hash, fullName);

        usersByUsername.put(username, newUser);
        persistAll();
        Logger.info("Registered new " + newUser.getRole() + " account: " + username);
        return newUser;
    }

    public User login(String username, String plainPassword) throws InvalidCredentialsException {
        User user = usersByUsername.get(username);
        if (user == null || !PasswordUtil.matches(plainPassword, user.getPasswordHash())) {
            Logger.warn("Failed login attempt for username: " + username);
            throw new InvalidCredentialsException("Invalid username or password.");
        }
        Logger.info("User logged in: " + username + " (" + user.getRole() + ")");
        return user;
    }

    private void persistAll() {
        List<String> records = new java.util.ArrayList<>();
        for (User user : usersByUsername.values()) {
            records.add(user.toRecord());
        }
        FileStorage.writeLines(USERS_FILE, records);
    }
}
