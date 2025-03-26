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

<dependency>
    <groupId>org.reflections</groupId>
    <artifactId>reflections</artifactId>
    <version>0.10.2</version>
</dependency>
```

## 🚀 Quick Start
1. Annotate Your API
Add @StressTest to any method you want to load test

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
| **Parameter** | Type   | **Description**               |
| ------------- | ------ | ----------------------------- |
| threads       | int    | Number of threads             |
| iterations    | int    | Number of requests per thread |
| path          | String | API endpoint                  |

## 📊 Monitoring
The test report will show:

- Threads
- Iterations per Thread
- Total Request
- Successful
- Success Rate
- Average Latency
- Total Test Time

