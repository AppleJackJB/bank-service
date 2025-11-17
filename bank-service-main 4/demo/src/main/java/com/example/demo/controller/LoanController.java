package com.example.demo.controller;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/loans")
@Transactional
public class LoanController {
    
    @Autowired
    private LoanRepository loanRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;

    // ВЗЯТЬ КРЕДИТ
    @PostMapping("/take")
    public ResponseEntity<?> takeLoan(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount,
            @RequestParam Integer termMonths,
            @RequestParam Double interestRate) {
        
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        // Зачисляем кредит на счет
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        
        // Создаем запись о кредите
        Loan loan = new Loan(account, amount, termMonths, interestRate);
        loan.setStatus("ACTIVE");
        loanRepository.save(loan);
        
        // Создаем транзакцию
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("LOAN_DISBURSEMENT");
        transaction.setDescription("Loan for " + termMonths + " months at " + interestRate + "%");
        transaction.setToAccount(account);
        transactionRepository.save(transaction);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Loan issued successfully");
        response.put("loanId", loan.getId());
        response.put("monthlyPayment", calculateMonthlyPayment(amount, termMonths, interestRate));
        
        return ResponseEntity.ok(response);
    }

    // ПОГАСИТЬ КРЕДИТ
    @PostMapping("/{loanId}/repay")
    public ResponseEntity<?> repayLoan(
            @PathVariable Long loanId,
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount) {
        
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new RuntimeException("Loan not found"));
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        // Проверяем достаточно ли средств
        if (account.getBalance().compareTo(amount) < 0) {
            return ResponseEntity.badRequest().body("Insufficient funds");
        }
        
        // Списываем средства
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        
        // Уменьшаем остаток по кредиту
        loan.setRemainingAmount(loan.getRemainingAmount().subtract(amount));
        
        if (loan.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus("PAID");
        }
        
        loanRepository.save(loan);
        
        // Создаем транзакцию
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("LOAN_REPAYMENT");
        transaction.setDescription("Loan repayment");
        transaction.setFromAccount(account);
        transactionRepository.save(transaction);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Loan repayment successful");
        response.put("remainingAmount", loan.getRemainingAmount());
        
        return ResponseEntity.ok(response);
    }

    // ПОЛУЧИТЬ ВСЕ КРЕДИТЫ КЛИЕНТА
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerLoans(@PathVariable Long customerId) {
        return ResponseEntity.ok(loanRepository.findByAccount_Customer_Id(customerId));
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, Integer termMonths, Double interestRate) {
        double monthlyRate = interestRate / 100 / 12;
        double payment = amount.doubleValue() * (monthlyRate * Math.pow(1 + monthlyRate, termMonths)) 
                        / (Math.pow(1 + monthlyRate, termMonths) - 1);
        return BigDecimal.valueOf(payment);
    }
}