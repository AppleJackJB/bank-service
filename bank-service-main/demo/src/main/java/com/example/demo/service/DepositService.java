package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class DepositService {

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Map<String, Object> openDeposit(Long accountId, BigDecimal amount,
                                           Double interestRate, Integer termMonths) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Deposit deposit = new Deposit(account, amount, interestRate, termMonths);
        depositRepository.save(deposit);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("DEPOSIT_OPENING");
        transaction.setDescription("Deposit opening for " + termMonths + " months at " + interestRate + "%");
        transaction.setFromAccount(account);
        transactionRepository.save(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Deposit opened successfully");
        response.put("depositId", deposit.getId());
        response.put("maturityDate", deposit.getEndDate());
        response.put("finalAmount", calculateFinalAmount(amount, interestRate, termMonths));

        return response;
    }

    public Map<String, Object> closeDeposit(Long depositId) {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new RuntimeException("Deposit not found"));

        if (!"ACTIVE".equals(deposit.getStatus())) {
            throw new IllegalArgumentException("Deposit is not active");
        }

        BigDecimal finalAmount = calculateFinalAmount(
                deposit.getAmount(),
                deposit.getInterestRate(),
                deposit.getTermMonths()
        );

        Account account = deposit.getAccount();
        account.setBalance(account.getBalance().add(finalAmount));
        accountRepository.save(account);

        deposit.setStatus("COMPLETED");
        depositRepository.save(deposit);

        Transaction transaction = new Transaction();
        transaction.setAmount(finalAmount);
        transaction.setTransactionType("DEPOSIT_CLOSING");
        transaction.setDescription("Deposit closing with interest");
        transaction.setToAccount(account);
        transactionRepository.save(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Deposit closed successfully");
        response.put("finalAmount", finalAmount);
        response.put("interestEarned", finalAmount.subtract(deposit.getAmount()));

        return response;
    }

    public Object getCustomerDeposits(Long customerId) {
        return depositRepository.findByAccount_Customer_Id(customerId);
    }

    private BigDecimal calculateFinalAmount(BigDecimal amount, Double interestRate, Integer termMonths) {
        BigDecimal interest = amount.multiply(BigDecimal.valueOf(interestRate))
                .multiply(BigDecimal.valueOf(termMonths))
                .divide(BigDecimal.valueOf(1200), 2, BigDecimal.ROUND_HALF_UP);
        return amount.add(interest);
    }
}