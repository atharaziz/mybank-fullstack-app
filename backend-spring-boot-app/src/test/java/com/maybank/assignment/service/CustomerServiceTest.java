package com.maybank.assignment.service;

import com.maybank.assignment.model.Customer;
import com.maybank.assignment.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;
    private Date dateOfBirth;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        dateOfBirth = new Date();
        customer = new Customer("John Doe", "john.doe@example.com", "1234567890", dateOfBirth);
    }

    @Test
    void testCreateCustomer_Success() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer createdCustomer = customerService.createCustomer(
                "John Doe", "john.doe@example.com", "1234567890", dateOfBirth);

        assertNotNull(createdCustomer);
        assertEquals("John Doe", createdCustomer.getName());
        assertEquals("john.doe@example.com", createdCustomer.getEmail());
        assertEquals("1234567890", createdCustomer.getPhone());

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testGetCustomerById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Optional<Customer> foundCustomer = customerService.getCustomerById(1L);

        assertTrue(foundCustomer.isPresent());
        assertEquals("John Doe", foundCustomer.get().getName());
        assertEquals("john.doe@example.com", foundCustomer.get().getEmail());

        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCustomerById_NotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Customer> foundCustomer = customerService.getCustomerById(1L);

        assertFalse(foundCustomer.isPresent());

        verify(customerRepository, times(1)).findById(1L);
    }
}
