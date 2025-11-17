package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CsrfController {
    
    @GetMapping("/api/csrf-token")
    public ResponseEntity<?> getCsrfToken(CsrfToken csrfToken) {
        if (csrfToken != null) {
            Map<String, String> response = new HashMap<>();
            response.put("csrfToken", csrfToken.getToken());
            response.put("headerName", csrfToken.getHeaderName());
            response.put("parameterName", csrfToken.getParameterName());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().build();
    }
}