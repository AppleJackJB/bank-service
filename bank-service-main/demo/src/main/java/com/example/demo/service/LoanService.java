package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Map<String, Object> takeLoan(Long accountId, BigDecimal amount,
                                        Integer termMonths, Double interestRate) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        Loan loan = new Loan(account, amount, termMonths, interestRate);
        loan.setStatus("ACTIVE");
        loanRepository.save(loan);

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

        return response;
    }

    public Map<String, Object> repayLoan(Long loanId, Long accountId, BigDecimal amount) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        loan.setRemainingAmount(loan.getRemainingAmount().subtract(amount));

        if (loan.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus("PAID");
        }

        loanRepository.save(loan);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("LOAN_REPAYMENT");
        transaction.setDescription("Loan repayment");
        transaction.setFromAccount(account);
        transactionRepository.save(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Loan repayment successful");
        response.put("remainingAmount", loan.getRemainingAmount());

        return response;
    }

    public Object getCustomerLoans(Long customerId) {
        return loanRepository.findByAccount_Customer_Id(customerId);
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, Integer termMonths, Double interestRate) {
        double monthlyRate = interestRate / 100 / 12;
        double payment = amount.doubleValue() * (monthlyRate * Math.pow(1 + monthlyRate, termMonths))
                / (Math.pow(1 + monthlyRate, termMonths) - 1);
        return BigDecimal.valueOf(payment);
    }
}