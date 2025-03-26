package com.stress.dto;

import java.util.concurrent.atomic.AtomicLong;

public class StressTestResult {
    private final AtomicLong totalRequests = new AtomicLong();
    private final AtomicLong successCount = new AtomicLong();
    private final AtomicLong totalTime = new AtomicLong();

    public long getTotalRequests() {
        return totalRequests.get();
    }

    public long getSuccessCount() {
        return successCount.get();
    }

    public long getTotalTime() {
        return totalTime.get();
    }

    // Getters and adder methods
    public void addSuccess(long duration) {
        successCount.incrementAndGet();
        totalRequests.incrementAndGet();
        totalTime.addAndGet(duration);
    }

    public void addFailure() {
        totalRequests.incrementAndGet();
    }

    // Calculate average latency
    public double getAverageLatency() {
        return totalTime.get() / (double) successCount.get();
    }

    // Calculate success rate
    public double getSuccessRate() {
        return successCount.get() / (double) totalRequests.get();
    }
}