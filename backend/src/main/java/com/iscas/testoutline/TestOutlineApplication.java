package com.iscas.testoutline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TestOutlineApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestOutlineApplication.class, args);
    }
} 