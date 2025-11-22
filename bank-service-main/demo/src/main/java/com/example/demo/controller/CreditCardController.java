package com.example.demo.controller;

import com.example.demo.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/credit-cards")
@Transactional
public class CreditCardController {

    @Autowired
    private CreditCardService creditCardService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyForCreditCard(
            @RequestParam Long customerId,
            @RequestParam BigDecimal requestedLimit,
            @RequestParam String employmentType,
            @RequestParam BigDecimal monthlyIncome) {
        try {
            return ResponseEntity.ok(creditCardService.applyForCreditCard(
                    customerId, requestedLimit, employmentType, monthlyIncome));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/applications/{applicationId}/approve")
    public ResponseEntity<?> approveCreditCardApplication(
            @PathVariable Long applicationId,
            @RequestParam Long accountId,
            @RequestParam BigDecimal approvedLimit) {
        try {
            return ResponseEntity.ok(creditCardService.approveCreditCardApplication(
                    applicationId, accountId, approvedLimit));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/applications/{applicationId}/reject")
    public ResponseEntity<?> rejectCreditCardApplication(@PathVariable Long applicationId) {
        try {
            return ResponseEntity.ok(creditCardService.rejectCreditCardApplication(applicationId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/applications/customer/{customerId}")
    public ResponseEntity<?> getCustomerApplications(@PathVariable Long customerId) {
        return ResponseEntity.ok(creditCardService.getCustomerApplications(customerId));
    }

    @GetMapping("/applications")
    public ResponseEntity<?> getAllApplications() {
        return ResponseEntity.ok(creditCardService.getAllApplications());
    }
}