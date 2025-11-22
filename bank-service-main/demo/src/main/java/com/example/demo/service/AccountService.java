package com.example.demo.service;

import com.example.demo.entity.Account;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Transaction;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Account createAccount(Account account) {
        if (account.getCustomer() == null || account.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Customer is required");
        }

        Optional<Customer> customer = customerRepository.findById(account.getCustomer().getId());
        if (customer.isEmpty()) {
            throw new IllegalArgumentException("Customer not found");
        }

        account.setCustomer(customer.get());
        return accountRepository.save(account);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public Account updateAccount(Long id, Account accountDetails) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setAccountNumber(accountDetails.getAccountNumber());
        account.setBalance(accountDetails.getBalance());
        account.setAccountType(accountDetails.getAccountType());
        account.setIsActive(accountDetails.getIsActive());

        return accountRepository.save(account);
    }

    public void deleteAccount(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new RuntimeException("Account not found");
        }
        accountRepository.deleteById(id);
    }

    public List<Account> getAccountsByCustomer(Long customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    public Map<String, Object> accrueInterest(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!"SAVINGS".equals(account.getAccountType())) {
            throw new IllegalArgumentException("Only savings accounts earn interest");
        }

        BigDecimal interest = account.getBalance().multiply(new BigDecimal("0.05"))
                .divide(new BigDecimal("365"), 2, BigDecimal.ROUND_HALF_UP);

        account.setBalance(account.getBalance().add(interest));
        accountRepository.save(account);

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

        return response;
    }

    public Map<String, Object> closeAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException("Account must have zero balance to close");
        }

        account.setIsActive(false);
        accountRepository.save(account);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Account closed successfully");
        response.put("accountNumber", account.getAccountNumber());

        return response;
    }
}