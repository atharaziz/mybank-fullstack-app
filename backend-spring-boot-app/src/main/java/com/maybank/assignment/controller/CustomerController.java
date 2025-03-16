package com.maybank.assignment.controller;

import com.maybank.assignment.dto.CreateAccountRequest;
import com.maybank.assignment.exception.ResourceNotFoundException;
import com.maybank.assignment.model.Account;
import com.maybank.assignment.model.Customer;
import com.maybank.assignment.service.AccountService;
import com.maybank.assignment.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author athar.aziz
 * Controller class for handling operations such as customer and account management.
 * Exposes API endpoints for creating, retrieving, updating, and deleting customers and accounts.
 */
@RestController
@RequestMapping("/api/bank")
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    /**
     * Creates a new customer.
     *
     * @param customer the customer details to be created.
     * @param result binding result to capture validation errors.
     * @return ResponseEntity with the created customer or error message if validation fails.
     */
    @PostMapping("/createCustomer")
    public ResponseEntity<?> createCustomer(@Valid @RequestBody Customer customer, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            result.getAllErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append(" ");
            });
            return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
        }

        Customer createdCustomer = customerService.createCustomer(customer.getName(), customer.getEmail(), customer.getPhone(), customer.getDateOfBirth());
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param id the ID of the customer to retrieve.
     * @return ResponseEntity with the customer data or a 404 error if the customer is not found.
     */
    @GetMapping("/getCustomer/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Optional<Customer> customerOptional = customerService.getCustomerById(id);
        if (customerOptional.isPresent()) {
            return new ResponseEntity<>(customerOptional.get(), HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("Customer with id " + id + " not found.");
        }
    }

    /**
     * Retrieves all customers.
     *
     * @return ResponseEntity with the list of all customers or a 404 error if no customers are found.
     */
    @GetMapping("/getCustomers")
    public ResponseEntity<?> getCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            return new ResponseEntity<>("No customers found", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(customers, HttpStatus.OK);
        }
    }

    /**
     * Updates an existing customer by their ID.
     *
     * @param id the ID of the customer to update.
     * @param updatedCustomer the new customer data.
     * @return ResponseEntity with the updated customer or a 404 error if the customer is not found.
     */
    @PutMapping("/updateCustomer/{id}")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @RequestBody Customer updatedCustomer) {
        Optional<Customer> existingCustomerOptional = customerService.getCustomerById(id);

        if (existingCustomerOptional.isPresent()) {
            Customer existingCustomer = existingCustomerOptional.get();
            existingCustomer.setName(updatedCustomer.getName());
            existingCustomer.setEmail(updatedCustomer.getEmail());
            existingCustomer.setPhone(updatedCustomer.getPhone());
            existingCustomer.setDateOfBirth(updatedCustomer.getDateOfBirth());

            customerService.saveCustomer(existingCustomer);

            return new ResponseEntity<>(existingCustomer, HttpStatus.OK);
        } else {
            throw new ResourceNotFoundException("Customer with ID '" + id + "' not found");
        }
    }

    /**
     * Deletes a customer by their ID.
     *
     * @param id the ID of the customer to delete.
     * @return ResponseEntity with no content status if successful or a 404 error if the customer is not found.
     */
    @DeleteMapping("/deleteCustomer/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        Optional<Customer> existingCustomerOptional = customerService.getCustomerById(id);

        if (existingCustomerOptional.isPresent()) {
            customerService.deleteCustomerById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new ResourceNotFoundException("Customer with ID '" + id + "' not found");
        }
    }

    /**
     * Creates a new account for a customer.
     *
     * @param createAccountRequest the account creation request details.
     * @param result binding result to capture validation errors.
     * @return ResponseEntity with the created account or error message if validation fails.
     */
    @PostMapping("/createAccount")
    public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest createAccountRequest, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            result.getAllErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append(" ");
            });
            return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
        }
        Account account = accountService.createAccount(createAccountRequest);
        return new ResponseEntity<>(account, HttpStatus.CREATED);
    }

    /**
     * Deposits cash into an account.
     *
     * @param accountNumber the account number to deposit cash into.
     * @param amount the amount to deposit.
     * @return the updated account after deposit.
     */
    @PostMapping("/depositCash")
    public Account depositCash(@RequestParam String accountNumber, @RequestParam Double amount) {
        return accountService.depositCash(accountNumber, amount);
    }

    /**
     * Withdraws cash from an account.
     *
     * @param accountNumber the account number to withdraw cash from.
     * @param amount the amount to withdraw.
     * @return the updated account after withdrawal.
     */
    @PostMapping("/withdrawCash")
    public Account withdrawCash(@RequestParam String accountNumber, @RequestParam Double amount) {
        return accountService.withdrawCash(accountNumber, amount);
    }

    /**
     * Closes an account.
     *
     * @param accountNumber the account number to close.
     * @return the closed account.
     */
    @PostMapping("/closeAccount")
    public Account closeAccount(@RequestParam String accountNumber) {
        return accountService.closeAccount(accountNumber);
    }

    /**
     * Retrieves account details by account number.
     *
     * @param accountNumber the account number to retrieve.
     * @return ResponseEntity with the account details or a 404 error if the account is not found.
     */
    @GetMapping("/getAccount/{accountNumber}")
    public ResponseEntity<?> getAccountByNumber(@PathVariable String accountNumber) {
        Account account = accountService.getAccountByNumber(accountNumber);
        if (account == null) {
            throw new ResourceNotFoundException("Account with number '" + accountNumber + "' not found");
        }

        return new ResponseEntity<>(account, HttpStatus.OK);
    }
}
