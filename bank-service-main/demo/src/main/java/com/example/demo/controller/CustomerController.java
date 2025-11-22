package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;
import com.example.demo.entity.Customer;
import com.example.demo.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/customers")
@Transactional(readOnly = true)
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    @Transactional
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        try {
            Customer savedCustomer = customerService.createCustomer(customer);
            return ResponseEntity.ok(savedCustomer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        return customer.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer updatedCustomer) {
        try {
            Customer savedCustomer = customerService.updateCustomer(id, updatedCustomer);
            return ResponseEntity.ok(savedCustomer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok("Customer deleted");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}