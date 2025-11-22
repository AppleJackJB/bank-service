package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import com.example.demo.entity.Account;
import com.example.demo.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
@Transactional(readOnly = true)
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    @Transactional
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        try {
            Account savedAccount = accountService.createAccount(account);
            return ResponseEntity.ok(savedAccount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountService.getAccountById(id);
        return account.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account accountDetails) {
        try {
            Account updatedAccount = accountService.updateAccount(id, accountDetails);
            return ResponseEntity.ok(updatedAccount);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        try {
            accountService.deleteAccount(id);
            return ResponseEntity.ok("Account deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customer/{customerId}")
    public List<Account> getAccountsByCustomer(@PathVariable Long customerId) {
        return accountService.getAccountsByCustomer(customerId);
    }

    @PostMapping("/{accountId}/accrue-interest")
    @Transactional
    public ResponseEntity<?> accrueInterest(@PathVariable Long accountId) {
        try {
            return ResponseEntity.ok(accountService.accrueInterest(accountId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{accountId}/close-account")
    @Transactional
    public ResponseEntity<?> closeAccount(@PathVariable Long accountId) {
        try {
            return ResponseEntity.ok(accountService.closeAccount(accountId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}