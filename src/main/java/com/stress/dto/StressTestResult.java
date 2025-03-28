package com.stress.dto;

import java.util.concurrent.atomic.AtomicLong;

public class StressTestResult {
    private final AtomicLong totalRequests = new AtomicLong();
    private final AtomicLong successCount = new AtomicLong();
    private final AtomicLong totalTime = new AtomicLong();
    private final AtomicLong clientErrors = new AtomicLong(); // 4xx
    private final AtomicLong serverErrors = new AtomicLong(); // 5xx
    private final AtomicLong timeoutErrors = new AtomicLong();
    private final AtomicLong connectionErrors = new AtomicLong();

    public void addClientError() {
        clientErrors.incrementAndGet();
        totalRequests.incrementAndGet();
    }

    public void addServerError() {
        serverErrors.incrementAndGet();
        totalRequests.incrementAndGet();
    }

    public void addTimeoutError() {
        timeoutErrors.incrementAndGet();
        totalRequests.incrementAndGet();
    }

    public void addConnectionError() {
        connectionErrors.incrementAndGet();
        totalRequests.incrementAndGet();
    }

    public long getClientErrors() { return clientErrors.get(); }
    public long getServerErrors() { return serverErrors.get(); }
    public long getTimeoutErrors() { return timeoutErrors.get(); }
    public long getConnectionErrors() { return connectionErrors.get(); }

    public long getTotalRequests() {
        return totalRequests.get();
    }

    public long getSuccessCount() {
        return successCount.get();
    }

    public long getTotalTime() {
        return totalTime.get();
    }

    public void addSuccess(long duration) {
        successCount.incrementAndGet();
        totalRequests.incrementAndGet();
        totalTime.addAndGet(duration);
    }

    public void addFailure() {
        totalRequests.incrementAndGet();
    }

    public double getAverageLatency() {
        long successes = successCount.get();
        return successes == 0 ? 0.0 : (totalTime.get() / (double) successes);
    }

    public double getSuccessRate() {
        return successCount.get() / (double) totalRequests.get();
    }
}