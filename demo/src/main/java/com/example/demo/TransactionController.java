package com.example.demo;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TransactionController {
    private List<Transaction> transactions = new ArrayList<>();
    private long nextId = 1;

    @PostMapping("/transactions")
    public Transaction createTransaction(@RequestBody Transaction transaction) {
        transaction.setId(nextId++);
        transactions.add(transaction);
        return transaction;
    }

    @GetMapping("/transactions")
    public List<Transaction> getAllTransactions() {
        return transactions;
    }

    @GetMapping("/transactions/{id}")
    public Transaction getTransactionById(@PathVariable long id) {
        for (Transaction transaction : transactions) {
            if (transaction.getId() == id) {
                return transaction;
            }
        }
        return null;
    }

    @PutMapping("/transactions/{id}")
    public Transaction updateTransaction(@PathVariable long id, @RequestBody Transaction updatedTransaction) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId() == id) {
                updatedTransaction.setId(id);
                transactions.set(i, updatedTransaction);
                return updatedTransaction;
            }
        }
        return null;
    }

    @DeleteMapping("/transactions/{id}")
    public String deleteTransaction(@PathVariable long id) {
        transactions.removeIf(transaction -> transaction.getId() == id);
        return "Transaction deleted";
    }
}

class Transaction {
    private long id;
    private double amount;
    private long fromAccountId;
    private long toAccountId;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public long getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(long fromAccountId) { this.fromAccountId = fromAccountId; }
    public long getToAccountId() { return toAccountId; }
    public void setToAccountId(long toAccountId) { this.toAccountId = toAccountId; }
}
