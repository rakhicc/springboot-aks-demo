package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "Hello from Spring Boot running on AKS!";
    }

    @GetMapping("/health")
    public String health() {
        return "Application is healthy ✅";
    }
    @GetMapping("/{name}")
    public String getName(@PathVariable String name) {
        return "Hello "+name;
    }
}