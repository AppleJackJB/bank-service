package com.example.demo.repository;

import com.example.demo.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DepositRepository extends JpaRepository<Deposit, Long> {
    List<Deposit> findByAccountId(Long accountId);
    List<Deposit> findByStatus(String status);
    List<Deposit> findByAccount_Customer_Id(Long customerId);
}