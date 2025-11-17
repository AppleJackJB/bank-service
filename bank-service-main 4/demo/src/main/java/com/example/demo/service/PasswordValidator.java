package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");

    public boolean isValid(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            return false;
        }
        return SPECIAL_CHAR_PATTERN.matcher(password).find();
    }

    public String getPasswordRequirements() {
        return "Пароль должен содержать минимум " + MIN_LENGTH + " символов и хотя бы один специальный символ (!@#$%^&*(),.?:{}|<>)";
    }
}