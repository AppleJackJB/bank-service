package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "credit_card_applications")
public class CreditCardApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @Column(name = "requested_limit")
    private BigDecimal requestedLimit;
    
    @Column(name = "employment_type")
    private String employmentType;
    
    @Column(name = "monthly_income")
    private BigDecimal monthlyIncome;
    
    @Column(name = "application_date")
    private LocalDateTime applicationDate;
    
    private String status;
    
    @Column(name = "approved_limit")
    private BigDecimal approvedLimit;
    
    // Конструкторы
    public CreditCardApplication() {}
    
    public CreditCardApplication(Customer customer, BigDecimal requestedLimit, String employmentType, BigDecimal monthlyIncome) {
        this.customer = customer;
        this.requestedLimit = requestedLimit;
        this.employmentType = employmentType;
        this.monthlyIncome = monthlyIncome;
        this.applicationDate = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public BigDecimal getRequestedLimit() { return requestedLimit; }
    public void setRequestedLimit(BigDecimal requestedLimit) { this.requestedLimit = requestedLimit; }
    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
    public BigDecimal getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }
    public LocalDateTime getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDateTime applicationDate) { this.applicationDate = applicationDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getApprovedLimit() { return approvedLimit; }
    public void setApprovedLimit(BigDecimal approvedLimit) { this.approvedLimit = approvedLimit; }
}