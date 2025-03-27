package com.stress.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StressTest {
    int threads() default 10; // 控制并发线程数量（模拟用户数）
    int iterations() default 100; // 定义单个线程的请求执行次数
    String path() default ""; // 指定被测接口的URL路径
    String method() default "GET"; // 定义HTTP请求方法（GET/POST/PUT等）
    String[] headers() default {}; // 格式示例: {"Content-Type: application/json"}  自定义HTTP请求头
    String body() default ""; // 设置HTTP请求体内容（用于POST/PUT等需要请求体的方法）
}