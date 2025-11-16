package com.example.demo.repository;

import com.example.demo.entity.CreditCardApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CreditCardApplicationRepository extends JpaRepository<CreditCardApplication, Long> {
    List<CreditCardApplication> findByCustomerId(Long customerId);
    List<CreditCardApplication> findByStatus(String status);
}