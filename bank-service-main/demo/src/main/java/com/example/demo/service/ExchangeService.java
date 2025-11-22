package com.example.demo.service;

import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class ExchangeService {

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public String setExchangeRate(String fromCurrency, String toCurrency, BigDecimal rate) {
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

        return "Exchange rate set: " + fromCurrency + " to " + toCurrency + " = " + rate;
    }

    public Map<String, Object> convertCurrency(Long fromAccountId, Long toAccountId, BigDecimal amount,
                                               String fromCurrency, String toCurrency) {
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("From account not found"));
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("To account not found"));

        ExchangeRate exchangeRate = exchangeRateRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency)
                .orElseThrow(() -> new RuntimeException("Exchange rate not found"));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        BigDecimal convertedAmount = amount.multiply(exchangeRate.getRate());

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(convertedAmount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

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

        return response;
    }

    public Optional<ExchangeRate> getExchangeRate(String fromCurrency, String toCurrency) {
        return exchangeRateRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency);
    }

    public Object getAllExchangeRates() {
        return exchangeRateRepository.findAll();
    }
}