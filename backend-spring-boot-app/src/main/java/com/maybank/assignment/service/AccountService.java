package com.maybank.assignment.service;

import com.maybank.assignment.dto.CreateAccountRequest;
import com.maybank.assignment.exception.ResourceNotFoundException;
import com.maybank.assignment.model.Account;
import com.maybank.assignment.model.Customer;
import com.maybank.assignment.repository.AccountRepository;
import com.maybank.assignment.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // Create Account
    public Account createAccount(CreateAccountRequest createAccountRequest) {
        // Validate if the customer exists
        Customer customer = customerRepository.findById(createAccountRequest.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer with ID " + createAccountRequest.getCustomerId() + " not found"));


        Account account = new Account();
        account.setCustomer(customer);
        account.setAccountNumber(generateAccountNumber());

        account.setAccountType(createAccountRequest.getAccountType());
        account.setBalance(0.0);
        account.setStatus("Active");  // Default status

        // Save account to repository
        return accountRepository.save(account);
    }

    private String generateAccountNumber() {
        return "AC" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Deposit Cash
    public Account depositCash(String accountNumber, Double amount) {

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account ==  null) {
            throw new ResourceNotFoundException("Account with number '" + accountNumber + "' not found");
        }
        if (account.getStatus().equals("Closed")) {
            throw new IllegalArgumentException("Account with number '" + accountNumber + "' is closed");

        }
            account.setBalance(account.getBalance() + amount);
            return accountRepository.save(account);
    }

    // Withdraw Cash
    public Account withdrawCash(String accountNumber, Double amount) {

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account ==  null) {
            throw new ResourceNotFoundException("Account with number '" + accountNumber + "' not found");
        }
        if (account.getStatus().equals("Active") && account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);
            return accountRepository.save(account);
        }
        throw new IllegalArgumentException("Insufficient balance or account is closed.");
    }

    // Close Account
    public Account closeAccount(String accountNumber) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new IllegalArgumentException("Account number cannot be null or empty");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new ResourceNotFoundException("Account with number '" + accountNumber + "' not found");
        }
        // Check if account is already closed
        if ("Closed".equalsIgnoreCase(account.getStatus())) {
            throw new IllegalArgumentException("Account with number '" + accountNumber + "' is already closed");
        }
        account.setStatus("Closed");
        return accountRepository.save(account);
    }

    // Inquire Account
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }
}