package com.example.demo;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AccountController {
    private List<Account> accounts = new ArrayList<>();
    private long nextId = 1;

    @PostMapping("/accounts")
    public Account createAccount(@RequestBody Account account) {
        account.setId(nextId++);
        accounts.add(account);
        return account;
    }

    @GetMapping("/accounts")
    public List<Account> getAllAccounts() {
        return accounts;
    }

    @GetMapping("/accounts/{id}")
    public Account getAccountById(@PathVariable long id) {
        for (Account account : accounts) {
            if (account.getId() == id) {
                return account;
            }
        }
        return null;
    }

    @PutMapping("/accounts/{id}")
    public Account updateAccount(@PathVariable long id, @RequestBody Account updatedAccount) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getId() == id) {
                updatedAccount.setId(id);
                accounts.set(i, updatedAccount);
                return updatedAccount;
            }
        }
        return null;
    }

    @DeleteMapping("/accounts/{id}")
    public String deleteAccount(@PathVariable long id) {
        accounts.removeIf(account -> account.getId() == id);
        return "Account deleted";
    }
}

class Account {
    private long id;
    private String accountNumber;
    private double balance;
    private String type;
    private long customerId;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public long getCustomerId() { return customerId; }
    public void setCustomerId(long customerId) { this.customerId = customerId; }
}