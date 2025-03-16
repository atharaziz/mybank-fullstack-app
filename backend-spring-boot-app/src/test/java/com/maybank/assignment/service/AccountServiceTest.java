package com.maybank.assignment.service;

import com.maybank.assignment.dto.CreateAccountRequest;
import com.maybank.assignment.exception.ResourceNotFoundException;
import com.maybank.assignment.model.Account;
import com.maybank.assignment.model.Customer;
import com.maybank.assignment.repository.AccountRepository;
import com.maybank.assignment.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountService accountService;

    private Customer customer;
    private CreateAccountRequest createAccountRequest;
    private Account account;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");

        createAccountRequest = new CreateAccountRequest();
        createAccountRequest.setCustomerId(1L);
        createAccountRequest.setAccountType("Savings");

        account = new Account();
        account.setAccountNumber("AC12345678");
        account.setBalance(100.0);
        account.setStatus("Active");
    }

    @Test
    void testCreateAccount_Success() {
        when(customerRepository.findById(1L)).thenReturn(java.util.Optional.of(customer));

        Account mockAccount = new Account();
        mockAccount.setAccountNumber("AC12345678");
        mockAccount.setCustomer(customer);
        mockAccount.setAccountType("Savings");
        mockAccount.setBalance(0.0);
        mockAccount.setStatus("Active");

        when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);

        Account createdAccount = accountService.createAccount(createAccountRequest);

        assertNotNull(createdAccount);
        assertEquals("Savings", createdAccount.getAccountType());
        assertEquals("Active", createdAccount.getStatus());
        assertEquals(customer, createdAccount.getCustomer());
        assertEquals("AC12345678", createdAccount.getAccountNumber());

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testCreateAccount_CustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.createAccount(createAccountRequest);
        });
        assertEquals("Customer with ID 1 not found", exception.getMessage());
    }
    @Test
    void testCloseAccount_Success() {
        when(accountRepository.findByAccountNumber("AC12345678")).thenReturn(account);

        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account closedAccount = accountService.closeAccount("AC12345678");

        assertNotNull(closedAccount);
        assertEquals("Closed", closedAccount.getStatus());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testCloseAccount_AccountNumberNullOrEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.closeAccount(null);
        });
        assertEquals("Account number cannot be null or empty", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.closeAccount("");
        });
        assertEquals("Account number cannot be null or empty", exception.getMessage());
    }

    @Test
    void testCloseAccount_AccountNotFound() {
        when(accountRepository.findByAccountNumber("AC12345678")).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.closeAccount("AC12345678");
        });

        assertEquals("Account with number 'AC12345678' not found", exception.getMessage());
    }

    @Test
    void testCloseAccount_AccountAlreadyClosed() {
        account.setStatus("Closed");
        when(accountRepository.findByAccountNumber("AC12345678")).thenReturn(account);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.closeAccount("AC12345678");
        });

        assertEquals("Account with number 'AC12345678' is already closed", exception.getMessage());
    }
    @Test
    void testDepositCash_Success() {
        when(accountRepository.findByAccountNumber("AC12345678")).thenReturn(account);

        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account updatedAccount = accountService.depositCash("AC12345678", 50.0);

        assertNotNull(updatedAccount);
        assertEquals(150.0, updatedAccount.getBalance()); // Original balance 100 + deposited amount 50
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testDepositCash_InvalidAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.depositCash("AC12345678", null);
        });
        assertEquals("Amount must be greater than zero.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.depositCash("AC12345678", -1.0);
        });
        assertEquals("Amount must be greater than zero.", exception.getMessage());
    }

    @Test
    void testDepositCash_AccountNotFound() {
        when(accountRepository.findByAccountNumber("AC12345678")).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.depositCash("AC12345678", 50.0);
        });

        assertEquals("Account with number 'AC12345678' not found", exception.getMessage());
    }

    @Test
    void testDepositCash_AccountClosed() {
        account.setStatus("Closed");
        when(accountRepository.findByAccountNumber("AC12345678")).thenReturn(account);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.depositCash("AC12345678", 50.0);
        });

        assertEquals("Account with number 'AC12345678' is closed", exception.getMessage());
    }
    @Test
    void testWithdrawCash_Success() {
        when(accountRepository.findByAccountNumber("AC12345678")).thenReturn(account);

        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account updatedAccount = accountService.withdrawCash("AC12345678", 50.0);

        assertNotNull(updatedAccount);
        assertEquals(50.0, updatedAccount.getBalance());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testWithdrawCash_InvalidAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.withdrawCash("AC12345678", null);
        });
        assertEquals("Amount must be greater than zero.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.withdrawCash("AC12345678", -1.0);
        });
        assertEquals("Amount must be greater than zero.", exception.getMessage());
    }

    @Test
    void testWithdrawCash_AccountNotFound() {
        when(accountRepository.findByAccountNumber("AC12345678")).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            accountService.withdrawCash("AC12345678", 50.0);
        });

        assertEquals("Account with number 'AC12345678' not found", exception.getMessage());
    }

    @Test
    void testWithdrawCash_InsufficientBalance() {
        account.setBalance(30.0);
        when(accountRepository.findByAccountNumber("AC12345678")).thenReturn(account);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.withdrawCash("AC12345678", 50.0); // Trying to withdraw 50.0
        });

        assertEquals("Insufficient balance or account is closed.", exception.getMessage());
    }

    @Test
    void testWithdrawCash_AccountClosed() {
        account.setStatus("Closed");
        when(accountRepository.findByAccountNumber("AC12345678")).thenReturn(account);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountService.withdrawCash("AC12345678", 50.0);
        });

        assertEquals("Insufficient balance or account is closed.", exception.getMessage());
    }
}
