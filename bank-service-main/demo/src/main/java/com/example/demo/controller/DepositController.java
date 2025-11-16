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
@RequestMapping("/deposits")
@Transactional
public class DepositController {
    
    @Autowired
    private DepositRepository depositRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;

    // ОТКРЫТЬ ВКЛАД
    @PostMapping("/open")
    public ResponseEntity<?> openDeposit(
            @RequestParam Long accountId,
            @RequestParam BigDecimal amount,
            @RequestParam Double interestRate,
            @RequestParam Integer termMonths) {
        
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        // Проверяем достаточно ли средств
        if (account.getBalance().compareTo(amount) < 0) {
            return ResponseEntity.badRequest().body("Insufficient funds");
        }
        
        // Списываем средства со счета
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        
        // Создаем вклад
        Deposit deposit = new Deposit(account, amount, interestRate, termMonths);
        depositRepository.save(deposit);
        
        // Создаем транзакцию
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
        
        return ResponseEntity.ok(response);
    }

    // ЗАКРЫТЬ ВКЛАД
    @PostMapping("/{depositId}/close")
    public ResponseEntity<?> closeDeposit(@PathVariable Long depositId) {
        Deposit deposit = depositRepository.findById(depositId)
            .orElseThrow(() -> new RuntimeException("Deposit not found"));
        
        if (!"ACTIVE".equals(deposit.getStatus())) {
            return ResponseEntity.badRequest().body("Deposit is not active");
        }
        
        // Расчет итоговой суммы
        BigDecimal finalAmount = calculateFinalAmount(
            deposit.getAmount(), 
            deposit.getInterestRate(), 
            deposit.getTermMonths()
        );
        
        // Возвращаем средства на счет
        Account account = deposit.getAccount();
        account.setBalance(account.getBalance().add(finalAmount));
        accountRepository.save(account);
        
        // Закрываем вклад
        deposit.setStatus("COMPLETED");
        depositRepository.save(deposit);
        
        // Создаем транзакцию
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
        
        return ResponseEntity.ok(response);
    }

    // ПОЛУЧИТЬ ВКЛАДЫ КЛИЕНТА
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerDeposits(@PathVariable Long customerId) {
        return ResponseEntity.ok(depositRepository.findByAccount_Customer_Id(customerId));
    }

    private BigDecimal calculateFinalAmount(BigDecimal amount, Double interestRate, Integer termMonths) {
        // Простые проценты
        BigDecimal interest = amount.multiply(BigDecimal.valueOf(interestRate))
                                  .multiply(BigDecimal.valueOf(termMonths))
                                  .divide(BigDecimal.valueOf(1200), 2, BigDecimal.ROUND_HALF_UP);
        return amount.add(interest);
    }
}