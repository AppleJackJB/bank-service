package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @PostMapping("/csrf-check")
    public String testCsrf(@RequestBody TestRequest request) {
        return "CSRF protection works! Received: " + request.getMessage();
    }

    public static class TestRequest {
        private String message;
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}