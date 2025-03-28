package com.stress.util;

public class ProgressBar {
    private final long total;
    private long current = 0;
    private final int width = 50;

    public ProgressBar(long total) {
        this.total = total;
    }

    public synchronized void update(long current) {
        this.current = current;
        print();
    }

    private void print() {
        double progress = Math.min((double) current / total, 1.0);
        int filledLength = (int) (progress * width);
        String bar = "[" + "=".repeat(filledLength) +
                (filledLength < width ? ">" : "") +
                " ".repeat(width - filledLength) + "]";
        String percent = String.format("%.1f%%", progress * 100);
        String info = String.format(" %s (%d/%d)", percent, current, total);
        String brightBlueColorCode = "\u001B[94m";
        System.out.println(brightBlueColorCode + "\r" + bar + info + "\u001B[0m");
        System.out.flush();
    }

    public void complete() {
        current = total;
        print();
        System.out.println();
    }
}