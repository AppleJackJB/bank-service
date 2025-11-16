package com.example.demo.controller;
import org.springframework.web.bind.annotation.*;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.Account;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transactions")
@Transactional(readOnly = true)
public class TransactionController {
    
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<Transaction> createTransaction(@RequestBody Transaction transaction) {
        // Проверяем существование счетов
        if (transaction.getFromAccount() != null && transaction.getFromAccount().getId() != null) {
            Optional<Account> fromAccount = accountRepository.findById(transaction.getFromAccount().getId());
            if (fromAccount.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            transaction.setFromAccount(fromAccount.get());
        }
        
        if (transaction.getToAccount() != null && transaction.getToAccount().getId() != null) {
            Optional<Account> toAccount = accountRepository.findById(transaction.getToAccount().getId());
            if (toAccount.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            transaction.setToAccount(toAccount.get());
        }
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return ResponseEntity.ok(savedTransaction);
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        return transaction.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction transactionDetails) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(id);
        if (optionalTransaction.isPresent()) {
            Transaction transaction = optionalTransaction.get();
            transaction.setAmount(transactionDetails.getAmount());
            transaction.setTransactionType(transactionDetails.getTransactionType());
            transaction.setDescription(transactionDetails.getDescription());
            Transaction updatedTransaction = transactionRepository.save(transaction);
            return ResponseEntity.ok(updatedTransaction);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Transactional 
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return ResponseEntity.ok("Transaction deleted");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/account/{accountNumber}")
    public List<Transaction> getTransactionsByAccount(@PathVariable String accountNumber) {
        return transactionRepository.findByFromAccountAccountNumberOrToAccountAccountNumber(accountNumber, accountNumber);
    }
}