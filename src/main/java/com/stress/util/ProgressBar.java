package com.stress.util;

public class ProgressBar {
    private final long total;
    private long current = 0;
    private final int width = 50; // 进度条宽度

    public ProgressBar(long total) {
        this.total = total;
    }

    public synchronized void update(long current) { // 改为接受当前值而非增量
        this.current = current;
//        this.current = Math.min(current, total); // 防止超过总数
        print();
    }

    private void print() {
        double progress = Math.min((double) current / total, 1.0); // 确保不超过100%
        int filledLength = (int) (progress * width);
        String bar = "[" + "=".repeat(filledLength) +
                (filledLength < width ? ">" : "") + // 避免末尾出现多余箭头
                " ".repeat(width - filledLength) + "]";
        String percent = String.format("%.1f%%", progress * 100);
        String info = String.format(" %s (%d/%d)", percent, current, total);
        String brightBlueColorCode = "\u001B[94m"; // 亮蓝色
        System.out.println(brightBlueColorCode + "\r" + bar + info + "\u001B[0m"); // 亮蓝色
        System.out.flush(); // 添加此行确保立即刷新
    }

    public void complete() {
        current = total;
        print();
        System.out.println(); // 换行
    }
}