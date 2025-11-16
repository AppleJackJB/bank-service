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
import java.util.Random;

@RestController
@RequestMapping("/credit-cards")
@Transactional
public class CreditCardController {
    
    @Autowired
    private CreditCardApplicationRepository creditCardApplicationRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private CardRepository cardRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;

    // Заявка на кредитную карту
    @PostMapping("/apply")
    public ResponseEntity<?> applyForCreditCard(
            @RequestParam Long customerId,
            @RequestParam BigDecimal requestedLimit,
            @RequestParam String employmentType,
            @RequestParam BigDecimal monthlyIncome) {
        
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        // Создаем заявку
        CreditCardApplication application = new CreditCardApplication(
            customer, requestedLimit, employmentType, monthlyIncome
        );
        creditCardApplicationRepository.save(application);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Credit card application submitted");
        response.put("applicationId", application.getId());
        response.put("status", "PENDING");
        
        return ResponseEntity.ok(response);
    }

    // ОДОБРИТЬ
    @PostMapping("/applications/{applicationId}/approve")
    public ResponseEntity<?> approveCreditCardApplication(
            @PathVariable Long applicationId,
            @RequestParam Long accountId,
            @RequestParam BigDecimal approvedLimit) {
        
        CreditCardApplication application = creditCardApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        Account account = accountRepository.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
        
        // Одобряем заявку
        application.setStatus("APPROVED");
        application.setApprovedLimit(approvedLimit);
        creditCardApplicationRepository.save(application);
        
        // Создаем кредитную карту
        Card creditCard = new Card();
        creditCard.setCardNumber(generateCardNumber());
        creditCard.setCardHolderName(application.getCustomer().getFirstName().toUpperCase() + " " + 
                                   application.getCustomer().getLastName().toUpperCase());
        creditCard.setExpiryDate(LocalDate.now().plusYears(3));
        creditCard.setCvv(generateCVV());
        creditCard.setCardType("CREDIT");
        creditCard.setAccount(account);
        creditCard.setIsActive(true);
        
        cardRepository.save(creditCard);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Credit card application approved");
        response.put("cardNumber", creditCard.getCardNumber());
        response.put("creditLimit", approvedLimit);
        response.put("expiryDate", creditCard.getExpiryDate());
        
        return ResponseEntity.ok(response);
    }

    // ОТКЛОНИТЬ
    @PostMapping("/applications/{applicationId}/reject")
    public ResponseEntity<?> rejectCreditCardApplication(@PathVariable Long applicationId) {
        CreditCardApplication application = creditCardApplicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));
        
        application.setStatus("REJECTED");
        creditCardApplicationRepository.save(application);
        
        return ResponseEntity.ok("Credit card application rejected");
    }

    // ПОЛУЧИТЬ ЗАЯВКИ КЛИЕНТА
    @GetMapping("/applications/customer/{customerId}")
    public ResponseEntity<?> getCustomerApplications(@PathVariable Long customerId) {
        return ResponseEntity.ok(creditCardApplicationRepository.findByCustomerId(customerId));
    }

    // ПОЛУЧИТЬ ВСЕ ЗАЯВКИ
    @GetMapping("/applications")
    public ResponseEntity<?> getAllApplications() {
        return ResponseEntity.ok(creditCardApplicationRepository.findAll());
    }

    private String generateCardNumber() {
        Random random = new Random();
        return "5" + String.format("%015d", random.nextLong(1000000000000000L));
    }

    private String generateCVV() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }
}