package com.example.demo.repository;

import com.example.demo.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByAccountId(Long accountId);
    List<Loan> findByStatus(String status);
    List<Loan> findByAccount_Customer_Id(Long customerId);
}