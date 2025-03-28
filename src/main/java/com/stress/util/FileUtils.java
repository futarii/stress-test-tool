package com.stress.util;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileUtils {
    public static void writeReport(String content) {
        Path basePath = Paths.get(System.getProperty("user.dir"))
                .resolve("src")
                .resolve("test")
                .resolve("stress-test-result");

        try {
            Files.createDirectories(basePath);

            String filename = "stress-report-" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".txt";

            Path filePath = basePath.resolve(filename);

            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                writer.write(content);
                System.out.println("Report saved to: " + filePath.toAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Failed to write report: " + e.getMessage());
        }
    }
}