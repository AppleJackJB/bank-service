package com.example.demo.service;

import com.example.demo.entity.Transaction;
import com.example.demo.entity.Account;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getFromAccount() != null && transaction.getFromAccount().getId() != null) {
            Optional<Account> fromAccount = accountRepository.findById(transaction.getFromAccount().getId());
            if (fromAccount.isEmpty()) {
                throw new IllegalArgumentException("From account not found");
            }
            transaction.setFromAccount(fromAccount.get());
        }

        if (transaction.getToAccount() != null && transaction.getToAccount().getId() != null) {
            Optional<Account> toAccount = accountRepository.findById(transaction.getToAccount().getId());
            if (toAccount.isEmpty()) {
                throw new IllegalArgumentException("To account not found");
            }
            transaction.setToAccount(toAccount.get());
        }

        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        transaction.setAmount(transactionDetails.getAmount());
        transaction.setTransactionType(transactionDetails.getTransactionType());
        transaction.setDescription(transactionDetails.getDescription());

        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found");
        }
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        return transactionRepository.findByFromAccountAccountNumberOrToAccountAccountNumber(accountNumber, accountNumber);
    }
}