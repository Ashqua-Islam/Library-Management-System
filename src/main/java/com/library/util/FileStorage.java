package com.library.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;


public final class FileStorage {

    private FileStorage() {
        // utility class, no instances
    }

    public static void ensureDirectoryExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Reads every line of a text file. Returns an empty list (not null)
     * if the file does not exist yet, so first-run behaves gracefully.
     */
    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return lines;
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            Logger.error("Failed to read file " + filePath + ": " + e.getMessage());
        }
        return lines;
    }

    /**
     * Overwrites the given file with the supplied records, one per line.
     * Used after every mutating operation so data survives a restart.
     */
    public static void writeLines(String filePath, List<String> records) {
        Path path = Paths.get(filePath);
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                for (String record : records) {
                    writer.write(record);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            Logger.error("Failed to write file " + filePath + ": " + e.getMessage());
        }
    }

    public static void appendLine(String filePath, String record) {
        Path path = Paths.get(filePath);
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (BufferedWriter writer = Files.newBufferedWriter(
                    path, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                writer.write(record);
                writer.newLine();
            }
        } catch (IOException e) {
            Logger.error("Failed to append to file " + filePath + ": " + e.getMessage());
        }
    }
}
