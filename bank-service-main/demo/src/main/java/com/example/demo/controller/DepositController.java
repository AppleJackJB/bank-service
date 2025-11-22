package com.example.demo.controller;

import com.example.demo.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/deposits")
@Transactional
public class DepositController {

    @Autowired
    private DepositService depositService;

    @PostMapping("/open")
    public ResponseEntity<?> openDeposit(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount,
            @RequestParam Double interestRate,
            @RequestParam Integer termMonths) {
        try {
            return ResponseEntity.ok(depositService.openDeposit(accountId, amount, interestRate, termMonths));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{depositId}/close")
    public ResponseEntity<?> closeDeposit(@PathVariable Long depositId) {
        try {
            return ResponseEntity.ok(depositService.closeDeposit(depositId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerDeposits(@PathVariable Long customerId) {
        return ResponseEntity.ok(depositService.getCustomerDeposits(customerId));
    }
}