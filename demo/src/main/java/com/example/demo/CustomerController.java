package com.example.demo;

import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CustomerController {
    private List<Customer> customers = new ArrayList<>();
    private long nextId = 1;

    @PostMapping("/customers")
    public Customer createCustomer(@RequestBody Customer customer) {
        customer.setId(nextId++);
        customers.add(customer);
        return customer;
    }

    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return customers;
    }

    @GetMapping("/customers/{id}")
    public Customer getCustomerById(@PathVariable long id) {
        for (Customer customer : customers) {
            if (customer.getId() == id) {
                return customer;
            }
        }
        return null;
    }

    @PutMapping("/customers/{id}")
    public Customer updateCustomer(@PathVariable long id, @RequestBody Customer updatedCustomer) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId() == id) {
                updatedCustomer.setId(id);
                customers.set(i, updatedCustomer);
                return updatedCustomer;
            }
        }
        return null;
    }

    @DeleteMapping("/customers/{id}")
    public String deleteCustomer(@PathVariable long id) {
        customers.removeIf(customer -> customer.getId() == id);
        return "Customer deleted";
    }
}

class Customer {
    private long id;
    private String firstName;
    private String lastName;
    private String email;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}