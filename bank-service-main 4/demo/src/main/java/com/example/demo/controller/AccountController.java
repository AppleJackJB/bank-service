package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import com.example.demo.entity.Account;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/accounts")
@Transactional(readOnly = true)
public class AccountController {
    
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<Account> createAccount(@RequestBody Account account) {
        // Проверяем существование клиента
        if (account.getCustomer() == null || account.getCustomer().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Optional<Customer> customer = customerRepository.findById(account.getCustomer().getId());
        if (customer.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        account.setCustomer(customer.get());
        Account savedAccount = accountRepository.save(account);
        return ResponseEntity.ok(savedAccount);
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountRepository.findById(id);
        return account.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Account> updateAccount(@PathVariable Long id, @RequestBody Account accountDetails) {
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setAccountNumber(accountDetails.getAccountNumber());
            account.setBalance(accountDetails.getBalance());
            account.setAccountType(accountDetails.getAccountType());
            account.setIsActive(accountDetails.getIsActive());
            Account updatedAccount = accountRepository.save(account);
            return ResponseEntity.ok(updatedAccount);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        if (accountRepository.existsById(id)) {
            accountRepository.deleteById(id);
            return ResponseEntity.ok("Account deleted");
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/customer/{customerId}")
    public List<Account> getAccountsByCustomer(@PathVariable Long customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    // НАЧИСЛЕНИЕ ПРОЦЕНТОВ ПО ВКЛАДУ
    @PostMapping("/{accountId}/accrue-interest")
    @Transactional
    public ResponseEntity<?> accrueInterest(@PathVariable Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        // сберегательные счета получают проценты
        if (!"SAVINGS".equals(account.getAccountType())) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Only savings accounts earn interest");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // Расчет процентов
        BigDecimal interest = account.getBalance().multiply(new BigDecimal("0.05"))
            .divide(new BigDecimal("365"), 2, BigDecimal.ROUND_HALF_UP);
        
        account.setBalance(account.getBalance().add(interest));
        accountRepository.save(account);
        
        // Создаем транзакцию
        Transaction transaction = new Transaction();
        transaction.setAmount(interest);
        transaction.setTransactionType("INTEREST");
        transaction.setDescription("Daily interest accrual");
        transaction.setToAccount(account);
        transactionRepository.save(transaction);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Interest accrued successfully");
        response.put("interestAmount", interest);
        response.put("newBalance", account.getBalance());
        
        return ResponseEntity.ok(response);
    }

    // ЗАКРЫТИЕ СЧЕТА С ПРОВЕРКОЙ БАЛАНСА
    @PostMapping("/{accountId}/close-account")
    @Transactional
    public ResponseEntity<?> closeAccount(@PathVariable Long accountId) {
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Account must have zero balance to close");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        account.setIsActive(false);
        accountRepository.save(account);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Account closed successfully");
        response.put("accountNumber", account.getAccountNumber());
        
        return ResponseEntity.ok(response);
    }
}