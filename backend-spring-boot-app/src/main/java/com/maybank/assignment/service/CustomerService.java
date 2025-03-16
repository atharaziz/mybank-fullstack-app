package com.maybank.assignment.service;

import com.maybank.assignment.exception.ResourceNotFoundException;
import com.maybank.assignment.model.Customer;
import com.maybank.assignment.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // Create Customer
    public Customer createCustomer(String name, String email, String phone, java.util.Date dateOfBirth) {
        Customer customer = new Customer(name, email, phone, dateOfBirth);
        return customerRepository.save(customer);
    }

    // Inquire Customer
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
    public void deleteCustomerById(Long id) {
        Optional<Customer> customerOptional = customerRepository.findById(id);

        if (customerOptional.isPresent()) {
            customerRepository.deleteById(id); // Delete customer from the database
        } else {
            throw new ResourceNotFoundException("Customer with ID '" + id + "' not found");
        }
    }
}