# stress-test-tool
A lightweight microservice interface performance testing tool

## 📥 Installation

Add the following dependencies to your `pom.xml`:

```xml
<dependency>
    <groupId>com.stress</groupId>
    <artifactId>stress-test-tool</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 🚀 Quick Start
1. Annotate Your API
  Add @StressTest to any method you want to load test

  ```java
  @StressTest(
              threads = 100,
              iterations = 10,
              path = "http://localhost:8080/api/test",
              method = "GET"
  )
  ```

2. Create Test Runner
  Initialize the tester in your test class:

  ```java
  public class StressTestRunner {
      @Test
      public void runStressTests() throws Exception {
          StressTester.start("your package");
      }
  }
  ```
## ⚙️ Configuration Options
| **Parameter** | Type     | **Description**                             |
| ------------- | -------- | ------------------------------------------- |
| threads       | int      | Number of threads                           |
| iterations    | int      | Number of requests per thread               |
| path          | String   | API endpoint                                |
| method        | String   | HTTP request methods (GET, POST, PUT, etc.) |
| headers       | String[] | Custom HTTP request headers                 |
| body          | String   | HTTP request body content                   |

## 📊 Monitoring
The test report will show:

- API path and corresponding class/method name

- HTTP method used for the API

- Number of threads used in the test

- Iterations per thread

- Total requests made

- Number of successful requests and success rate percentage

- Average latency in milliseconds

- Number of client errors (4xx)

- Number of server errors (5xx)

- Number of timeout errors

- Number of connection errors

- Aggregated summary including total requests across all tests

- Total successful requests and overall success rate percentage

- Total client errors (4xx) across all tests

- Total server errors (5xx) across all tests

- Total timeouts across all tests

- Total connection errors across all tests

  

