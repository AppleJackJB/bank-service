package com.example.demo.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Column(name = "remaining_amount")
    private BigDecimal remainingAmount;
    
    @Column(name = "term_months")
    private Integer termMonths;
    
    @Column(name = "interest_rate")
    private Double interestRate;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    private String status; // PENDING, ACTIVE, PAID, DEFAULT
    
    // Конструкторы
    public Loan() {}
    
    public Loan(Account account, BigDecimal amount, Integer termMonths, Double interestRate) {
        this.account = account;
        this.amount = amount;
        this.remainingAmount = amount;
        this.termMonths = termMonths;
        this.interestRate = interestRate;
        this.startDate = LocalDate.now();
        this.endDate = LocalDate.now().plusMonths(termMonths);
        this.status = "PENDING";
    }
    
    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }
    public Integer getTermMonths() { return termMonths; }
    public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }
    public Double getInterestRate() { return interestRate; }
    public void setInterestRate(Double interestRate) { this.interestRate = interestRate; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}