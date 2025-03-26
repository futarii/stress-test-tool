package com.stress.core;

import com.stress.dto.StressTestResult;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class RequestWorker implements Runnable {
    private final String url;
    private final int iterations;
    private final StressTestResult result;

    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public RequestWorker(String url, int iterations, StressTestResult result) {
        this.url = url;
        this.iterations = iterations;
        this.result = result;
    }

    @Override
    public void run() {
        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(5))
                        .build();

                HttpResponse<String> response = client.send(
                        request, HttpResponse.BodyHandlers.ofString()
                );

                if (response.statusCode() == 200) {
                    result.addSuccess(System.currentTimeMillis() - start);
                } else {
                    result.addFailure();
                }
            } catch (Exception e) {
                result.addFailure();
            }
        }
    }
}