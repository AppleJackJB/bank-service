package com.example.demo.controller;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.entity.Customer;
import com.example.demo.service.PasswordValidator;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PasswordValidator passwordValidator;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        Map<String, String> response = new HashMap<>();

        // Проверяем существование пользователя
        if (userService.existsByUsername(request.getUsername())) {
            response.put("error", "Username already exists");
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.existsByEmail(request.getEmail())) {
            response.put("error", "Email already exists");
            return ResponseEntity.badRequest().body(response);
        }

        // Проверяем надежность пароля
        if (!passwordValidator.isValid(request.getPassword())) {
            response.put("error", "Weak password");
            response.put("requirements", passwordValidator.getPasswordRequirements());
            return ResponseEntity.badRequest().body(response);
        }

        // Создаем пользователя
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole() != null ? request.getRole() : "USER");

         String userRole = request.getRole() != null ? request.getRole() : "USER";
        if (!"ADMIN".equals(userRole)) {
            Customer customer = new Customer();
            customer.setFirstName(request.getFirstName());
            customer.setLastName(request.getLastName());
            customer.setEmail(request.getEmail());
            user.setCustomer(customer);
        }
        
        userService.save(user);
        
        response.put("message", "User registered successfully");
        return ResponseEntity.ok(response);
    }
}