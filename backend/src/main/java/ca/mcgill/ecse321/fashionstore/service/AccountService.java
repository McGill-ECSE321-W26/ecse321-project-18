package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.AccountRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.AccountResponseDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Account;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.model.Owner;
import ca.mcgill.ecse321.fashionstore.repository.AccountRepository;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OwnerRepository;
import jakarta.validation.Valid;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/** Service class for AccountService. */
@Service
@Validated
public class AccountService {
    private AccountRepository accountRepository;
    private OwnerRepository ownerRepository;
    private EmployeeRepository employeeRepository;
    private CustomerRepository customerRepository;

    /**
     * Constructor for AccountRepository class
     *
     * @param accountRepository account repository class
     * @author Qiuyu Huang (redacted24)
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public AccountService(AccountRepository accountRepository, OwnerRepository ownerRepository, EmployeeRepository employeeRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.ownerRepository = ownerRepository;
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Checks if email exists and the password matches. If both are valid, user is authenticated and
     * granted access to the system. Otherwise, user is denied access and an error message is shown.
     * @param requestDto
     * @return An AccoutResponseDTO with the id, email and the account type (employee, customer, owner).
     * @throws FashionStoreException if an account with the email isn't found, or a password doesn't match
     * @author Qiuyu Huang (redacted24)
     */
    public AccountResponseDto accoutLoginCheck(@Valid AccountRequestDto requestDto) {
        Account account = accountRepository.findAccountByEmail(requestDto.email());
        // Email check
        if (account == null) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST,
                    "An account with that email does not exist."
                    );
        }

        // Password check
        if (!account.getPassword().equals(requestDto.password())) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST,
                    "Password is incorrect."
            );
        }

        // Account found. Check account type.
        // Owner
        Owner owner = ownerRepository.findOwnerById(account.getId());
        if (owner != null) {
            return new AccountResponseDto(account.getId(), account.getEmail(), "Owner");
        }

        // Employee
        Employee employee = employeeRepository.findEmployeeById(account.getId());
        if (employee != null) {
            return new AccountResponseDto(account.getId(), account.getEmail(), "Employee");
        }

        // Customer
        Customer customer = customerRepository.findCustomerById(account.getId());
        if (customer != null) {
            return new AccountResponseDto(account.getId(), account.getEmail(), "Customer");
        }

        return new AccountResponseDto(account.getId(), account.getEmail(), "");
    }
}