package com.library.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public final class Logger {

    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = LOG_DIR + "/app.log";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Logger() {
        // utility class, no instances
    }

    public static void info(String message) {
        write("INFO", message);
    }

    public static void warn(String message) {
        write("WARN", message);
    }

    public static void error(String message) {
        write("ERROR", message);
    }

    private static void write(String level, String message) {
        FileStorage.ensureDirectoryExists(LOG_DIR);
        String line = String.format("[%s] %-5s %s",
                LocalDateTime.now().format(FORMATTER), level, message);

        // Always echo to console too, so the user sees activity live.
        System.out.println(line);

        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(line);
        } catch (IOException e) {
            // Logging must never crash the application; fall back to stderr only.
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}
