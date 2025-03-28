package com.stress.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StressTest {
    /**
     * 控制并发线程数量（模拟用户数）
     * @return 线程数，默认 10
     */
    int threads() default 10;

    /**
     * 定义单个线程的请求执行次数
     * @return 迭代次数，默认 100
     */
    int iterations() default 100;

    /**
     * 指定被测接口的 URL 路径
     * @return URL 路径，默认为空
     */
    String path() default "";

    /**
     * 定义 HTTP 请求方法（GET/POST/PUT 等）
     * @return HTTP 方法，默认 GET
     */
    String method() default "GET";

    /**
     * 自定义 HTTP 请求头（格式示例: {"Content-Type: application/json"}）
     * @return 请求头数组，默认为空
     */
    String[] headers() default {};

    /**
     * 设置 HTTP 请求体内容（用于 POST/PUT 等需要请求体的方法）
     * @return 请求体内容，默认为空
     */
    String body() default "";
}