package com.example.demo.controller;

import com.example.demo.service.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.example.demo.entity.ExchangeRate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/exchange")
@Transactional
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    @PostMapping("/rate")
    public ResponseEntity<?> setExchangeRate(
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency,
            @RequestParam BigDecimal rate) {
        return ResponseEntity.ok(exchangeService.setExchangeRate(fromCurrency, toCurrency, rate));
    }

    @PostMapping("/convert")
    public ResponseEntity<?> convertCurrency(
            @RequestParam Long fromAccountId,
            @RequestParam Long toAccountId,
            @RequestParam BigDecimal amount,
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency) {
        try {
            return ResponseEntity.ok(exchangeService.convertCurrency(
                    fromAccountId, toAccountId, amount, fromCurrency, toCurrency));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/rate/{fromCurrency}/{toCurrency}")
    public ResponseEntity<?> getExchangeRate(
            @PathVariable String fromCurrency,
            @PathVariable String toCurrency) {
        Optional<ExchangeRate> exchangeRate = exchangeService.getExchangeRate(fromCurrency, toCurrency);
        return exchangeRate.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rates")
    public ResponseEntity<?> getAllExchangeRates() {
        return ResponseEntity.ok(exchangeService.getAllExchangeRates());
    }
}