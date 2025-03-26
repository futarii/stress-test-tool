package com.stress.core;

import com.stress.annotation.StressTest;
import com.stress.dto.StressTestResult;
import com.stress.util.FileUtils;
import com.stress.util.ProgressBar;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StressTester {

    private static class TestResultInfo {
        String className;
        String methodName;
        StressTest config;
        StressTestResult result;

        public TestResultInfo(String className, String methodName, StressTest config, StressTestResult result) {
            this.className = className;
            this.methodName = methodName;
            this.config = config;
            this.result = result;
        }
    }

    public static void start(String basePackage) throws Exception {
        Reflections reflections = new Reflections(basePackage, new MethodAnnotationsScanner());
        Set<Method> methods = reflections.getMethodsAnnotatedWith(StressTest.class);

        List<TestResultInfo> allResults = new ArrayList<>();

        for (Method method : methods) {
            StressTest annotation = method.getAnnotation(StressTest.class);
            String className = method.getDeclaringClass().getName();
            String methodName = method.getName();
            StressTestResult result = runTest(annotation);
            allResults.add(new TestResultInfo(className, methodName, annotation, result));
        }

        String report = generateAggregateReport(allResults);
        FileUtils.writeReport(report);
    }

//    private static StressTestResult runTest(StressTest config) throws InterruptedException {
//        ExecutorService executor = Executors.newFixedThreadPool(config.threads());
//        StressTestResult result = new StressTestResult();
//
//        long startTime = System.currentTimeMillis();
//
//        for (int i = 0; i < config.threads(); i++) {
//            executor.submit(new RequestWorker(config, config.iterations(), result));
//        }
//
//        executor.shutdown();
//        executor.awaitTermination(1, TimeUnit.HOURS);
//
//        return result;
//    }

    private static StressTestResult runTest(StressTest config) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(config.threads());
        StressTestResult result = new StressTestResult();

        long totalRequests = config.threads() * config.iterations();
        ProgressBar progressBar = new ProgressBar(totalRequests);

        System.out.printf("\nStarting test: %s (%d threads × %d iterations = %d requests)%n",
                config.path(), config.threads(), config.iterations(), totalRequests);

        // 启动进度监控线程
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        // 修改进度更新方式（直接传递当前完成数）
        scheduler.scheduleAtFixedRate(() -> {
            long completed = result.getTotalRequests();
            progressBar.update(completed); // 直接传递当前值
        }, 0, 500, TimeUnit.MILLISECONDS);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < config.threads(); i++) {
            executor.submit(new RequestWorker(config, config.iterations(), result));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        // 停止进度监控
        scheduler.shutdown();
        progressBar.complete();

        return result;
    }

    private static String generateAggregateReport(List<TestResultInfo> allResults) {
        StringBuilder report = new StringBuilder();
        report.append("Aggregated Stress Test Report:\n");
        report.append("===============================\n\n");

        long totalRequests = 0;
        long totalSuccess = 0;
        long totalClientErrors = 0;
        long totalServerErrors = 0;
        long totalTimeouts = 0;
        long totalConnectionErrors = 0;

        for (TestResultInfo info : allResults) {
            StressTest config = info.config;
            StressTestResult result = info.result;

            report.append(String.format("API: %s (%s.%s)\n", config.path(), info.className, info.methodName));
            report.append(String.format("HTTP Method: %s\n", config.method()));
            report.append(String.format("Threads: %d\n", config.threads()));
            report.append(String.format("Iterations per Thread: %d\n", config.iterations()));
            report.append(String.format("Total Requests: %d\n", result.getTotalRequests()));
            report.append(String.format("Successful: %d (%.2f%%)\n", result.getSuccessCount(), result.getSuccessRate() * 100));
            report.append(String.format("Average Latency: %.2fms\n", result.getAverageLatency()));
            report.append(String.format("Client Errors (4xx): %d\n", result.getClientErrors()));
            report.append(String.format("Server Errors (5xx): %d\n", result.getServerErrors()));
            report.append(String.format("Timeouts: %d\n", result.getTimeoutErrors()));
            report.append(String.format("Connection Errors: %d\n", result.getConnectionErrors()));
            report.append("-----------------------------------\n\n");

            totalRequests += result.getTotalRequests();
            totalSuccess += result.getSuccessCount();
            totalClientErrors += result.getClientErrors();
            totalServerErrors += result.getServerErrors();
            totalTimeouts += result.getTimeoutErrors();
            totalConnectionErrors += result.getConnectionErrors();
        }

        report.append("Aggregated Summary:\n");
        report.append("===================\n");
        report.append(String.format("Total Requests: %d\n", totalRequests));
        report.append(String.format("Total Successful: %d (%.2f%%)\n", totalSuccess, (totalSuccess / (double) totalRequests) * 100));
        report.append(String.format("Total Client Errors (4xx): %d\n", totalClientErrors));
        report.append(String.format("Total Server Errors (5xx): %d\n", totalServerErrors));
        report.append(String.format("Total Timeouts: %d\n", totalTimeouts));
        report.append(String.format("Total Connection Errors: %d\n", totalConnectionErrors));

        return report.toString();
    }
}