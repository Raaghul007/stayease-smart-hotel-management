package com.Hotel.hotel_management.Service;


import com.Hotel.hotel_management.Entity.*;
import com.Hotel.hotel_management.Repository.*;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // Register customer
    public Customer addCustomer(Customer customer) {

        customer.setId(null);

        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already registered");
        }

        if (customerRepository.findByPhoneNumber(
                customer.getPhoneNumber()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Phone number already registered");
        }

        return customerRepository.save(customer);
    }

    // Get all customers
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Get customer by ID
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Customer not found"));
    }

    // Update customer
    public Customer updateCustomer(Long id, Customer updatedCustomer) {

        Customer customer = getCustomerById(id);

        customer.setCustomerName(updatedCustomer.getCustomerName());
        customer.setEmail(updatedCustomer.getEmail());
        customer.setPhoneNumber(updatedCustomer.getPhoneNumber());
        customer.setAddress(updatedCustomer.getAddress());

        return customerRepository.save(customer);
    }

    // Delete customer
    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
    }
}

