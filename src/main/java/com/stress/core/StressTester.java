package com.stress.core;

import com.stress.annotation.StressTest;
import com.stress.dto.StressTestResult;
import com.stress.util.FileUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StressTester {

    public static void start(String basePackage) throws Exception {
        Reflections reflections = new Reflections(basePackage, new MethodAnnotationsScanner());
        Set<Method> methods = reflections.getMethodsAnnotatedWith(StressTest.class);

        for (Method method : methods) {
            StressTest annotation = method.getAnnotation(StressTest.class);
            runTest(annotation);
        }
    }

    private static void runTest(StressTest config) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(config.threads());
        StressTestResult result = new StressTestResult();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < config.threads(); i++) {
            executor.submit(new RequestWorker(config, config.iterations(), result));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        long endTime = System.currentTimeMillis();

        String report = generateReport(config, result, endTime - startTime);
        FileUtils.writeReport(report);
    }

    private static String generateReport(StressTest config, StressTestResult result, long totalTime) {
        return String.format(
                "Stress Test Report:\n" +
                        "===================\n" +
                        "Threads: %d\n" +
                        "Iterations per Thread: %d\n" +
                        "Total Requests: %d\n" +
                        "Successful: %d\n" +
                        "Success Rate: %.2f%%\n" +
                        "Average Latency: %.2fms\n" +
                        "Client Errors (4xx): %d\n" +
                        "Server Errors (5xx): %d\n" +
                        "Timeouts: %d\n" +
                        "Connection Errors: %d\n" +
                        "Total Test Time: %dms",
                config.threads(),
                config.iterations(),
                result.getTotalRequests(),
                result.getSuccessCount(),
                result.getSuccessRate() * 100,
                result.getAverageLatency(),
                result.getClientErrors(),
                result.getServerErrors(),
                result.getTimeoutErrors(),
                result.getConnectionErrors(),
                totalTime
        );
    }
}