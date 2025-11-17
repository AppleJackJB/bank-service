package com.example.demo.controller;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/exchange")
@Transactional
public class ExchangeController {
    @Autowired
    private ExchangeRateRepository exchangeRateRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    // УСТАНОВИТЬ КУРС ВАЛЮТ
    @PostMapping("/rate")
    public ResponseEntity<?> setExchangeRate(
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency,
            @RequestParam BigDecimal rate) {
        
        Optional<ExchangeRate> existingRate = exchangeRateRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency);
        
        ExchangeRate exchangeRate;
        if (existingRate.isPresent()) {
            exchangeRate = existingRate.get();
            exchangeRate.setRate(rate);
            exchangeRate.setLastUpdated(LocalDateTime.now());
        } else {
            exchangeRate = new ExchangeRate(fromCurrency, toCurrency, rate);
        }
        
        exchangeRateRepository.save(exchangeRate);
        
        return ResponseEntity.ok("Exchange rate set: " + fromCurrency + " to " + toCurrency + " = " + rate);
    }

    // КОНВЕРТИРОВАТЬ ВАЛЮТУ
    @PostMapping("/convert")
    public ResponseEntity<?> convertCurrency(
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId, 
            @RequestParam BigDecimal amount,
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency) {
        
        Account fromAccount = accountRepository.findById(fromAccountId)
            .orElseThrow(() -> new RuntimeException("From account not found"));
        Account toAccount = accountRepository.findById(toAccountId)
            .orElseThrow(() -> new RuntimeException("To account not found"));
        
        // Получаем курс
        ExchangeRate exchangeRate = exchangeRateRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency)
            .orElseThrow(() -> new RuntimeException("Exchange rate not found"));
        
        // Проверяем достаточно ли средств
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            return ResponseEntity.badRequest().body("Insufficient funds");
        }
        
        // Конвертируем сумму
        BigDecimal convertedAmount = amount.multiply(exchangeRate.getRate());
        
        // Обновляем балансы
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(convertedAmount));
        
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        
        // Создаем транзакцию
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("CURRENCY_CONVERSION");
        transaction.setDescription(String.format("Currency conversion %s->%s at rate %s", 
            fromCurrency, toCurrency, exchangeRate.getRate()));
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transactionRepository.save(transaction);
        
        Map<String, Object> response = new HashMap<>();
        response.put("convertedAmount", convertedAmount);
        response.put("exchangeRate", exchangeRate.getRate());
        response.put("fromCurrency", fromCurrency);
        response.put("toCurrency", toCurrency);
        
        return ResponseEntity.ok(response);
    }

    // ПОЛУЧИТЬ КУРС ВАЛЮТ
    @GetMapping("/rate/{fromCurrency}/{toCurrency}")
    public ResponseEntity<?> getExchangeRate(
            @PathVariable String fromCurrency,
            @PathVariable String toCurrency) {
        
        Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency);
        
        if (exchangeRate.isPresent()) {
            return ResponseEntity.ok(exchangeRate.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ПОЛУЧИТЬ ВСЕ КУРСЫ
    @GetMapping("/rates")
    public ResponseEntity<?> getAllExchangeRates() {
        return ResponseEntity.ok(exchangeRateRepository.findAll());
    }
}