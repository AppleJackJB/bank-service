package com.example.demo.controller;

import com.example.demo.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/loans")
@Transactional
public class LoanController {

    @Autowired
    private LoanService loanService;

    @PostMapping("/take")
    public ResponseEntity<?> takeLoan(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount,
            @RequestParam Integer termMonths,
            @RequestParam Double interestRate) {
        try {
            return ResponseEntity.ok(loanService.takeLoan(accountId, amount, termMonths, interestRate));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{loanId}/repay")
    public ResponseEntity<?> repayLoan(
            @PathVariable Long loanId,
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount) {
        try {
            return ResponseEntity.ok(loanService.repayLoan(loanId, accountId, amount));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerLoans(@PathVariable Long customerId) {
        return ResponseEntity.ok(loanService.getCustomerLoans(customerId));
    }
}