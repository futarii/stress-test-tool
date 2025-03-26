package com.stress.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileUtils {
    public static void writeReport(String content) {
        String filename = "stress-report-" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content);
            System.out.println("Report saved to: " + filename);
        } catch (Exception e) {
            System.err.println("Failed to write report: " + e.getMessage());
        }
    }
}