package com.stress.core;

import com.stress.annotation.StressTest;
import com.stress.dto.StressTestResult;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RequestWorker implements Runnable {
    private final String url;
    private final int iterations;
    private final StressTestResult result;
    private final StressTest config;

    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // 修改构造函数接收完整配置
    public RequestWorker(StressTest config, int iterations, StressTestResult result) {
        this.url = config.path();
        this.iterations = iterations;
        this.result = result;
        this.config = config;
    }

    @Override
    public void run() {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            futures.add(sendAsyncRequest());
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private CompletableFuture<Void> sendAsyncRequest() {
        long start = System.currentTimeMillis();
        try {
            HttpRequest request = buildRequest(config);
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> handleResponse(response, start))
                    .exceptionally(ex -> {
                        handleException(ex, start);
                        return null;
                    });
        } catch (Exception e) {
            result.addFailure();
            return CompletableFuture.completedFuture(null);
        }
    }

    private HttpRequest buildRequest(StressTest config) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(5));

        String method = config.method().toUpperCase();
        if ("GET".equals(method)) {
            builder.GET();
        } else {
            builder.method(method, HttpRequest.BodyPublishers.ofString(config.body()));
        }

        for (String header : config.headers()) {
            String[] parts = header.split(":", 2);
            if (parts.length == 2) {
                builder.header(parts[0].trim(), parts[1].trim());
            }
        }

        return builder.build();
    }


    private void handleResponse(HttpResponse<String> response, long start) {
        long latency = System.currentTimeMillis() - start;
        int statusCode = response.statusCode();

        if (statusCode == 200) {
            result.addSuccess(latency);
        } else if (statusCode >= 400 && statusCode < 500) {
            result.addClientError();
        } else if (statusCode >= 500) {
            result.addServerError();
        } else {
            result.addFailure();
        }
    }

    private void handleException(Throwable ex, long start) {
        long latency = System.currentTimeMillis() - start;
        if (ex.getCause() instanceof java.net.http.HttpTimeoutException) {
            result.addTimeoutError();
        } else if (ex.getCause() instanceof java.io.IOException) {
            result.addConnectionError();
        } else {
            result.addFailure();
        }
    }
}