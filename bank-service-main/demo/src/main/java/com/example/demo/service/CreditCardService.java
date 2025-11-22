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
import java.util.Random;

@Service
@Transactional
public class CreditCardService {

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

    public Map<String, Object> applyForCreditCard(Long customerId, BigDecimal requestedLimit,
                                                  String employmentType, BigDecimal monthlyIncome) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        CreditCardApplication application = new CreditCardApplication(
                customer, requestedLimit, employmentType, monthlyIncome
        );
        creditCardApplicationRepository.save(application);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Credit card application submitted");
        response.put("applicationId", application.getId());
        response.put("status", "PENDING");

        return response;
    }

    public Map<String, Object> approveCreditCardApplication(Long applicationId, Long accountId,
                                                            BigDecimal approvedLimit) {
        CreditCardApplication application = creditCardApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        application.setStatus("APPROVED");
        application.setApprovedLimit(approvedLimit);
        creditCardApplicationRepository.save(application);

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

        return response;
    }

    public String rejectCreditCardApplication(Long applicationId) {
        CreditCardApplication application = creditCardApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus("REJECTED");
        creditCardApplicationRepository.save(application);

        return "Credit card application rejected";
    }

    public Object getCustomerApplications(Long customerId) {
        return creditCardApplicationRepository.findByCustomerId(customerId);
    }

    public Object getAllApplications() {
        return creditCardApplicationRepository.findAll();
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