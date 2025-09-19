package com.example.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@SpringBootApplication
@RestController
public class Application {

    @GetMapping("/")
    public Map<String, Object> hello() {
        return Map.of(
                "message", "Hello from Java 17 + Jenkins + Docker + Render!",
                "time", Instant.now().toString()
        );
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
