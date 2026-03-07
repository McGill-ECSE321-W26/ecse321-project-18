package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.AccountRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.AccountResponseDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.repository.AccountRepository;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/** Account Service class. */
@Service
@Validated
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * AccountService constructor.
     *
     * @param accountRepository AccountRepository required to access the database.
     * @param customerRepository CustomerRepository required to access the database.
     * @param employeeRepository EmployeeRepository required to access the database.
     * @author Aurore Zhang (ororio0)
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public AccountService(
            AccountRepository accountRepository,
            CustomerRepository customerRepository,
            EmployeeRepository employeeRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Service method to create a new employee account.
     *
     * @param accountRequestDto AccountRequestDto (email, password).
     * @author Aurore Zhang (ororio0)
     */
    @Transactional
    public AccountResponseDto createEmployeeAccount(@Valid AccountRequestDto accountRequestDto) {
        if (accountRepository.existsByEmail(accountRequestDto.email())) {
            throw new FashionStoreException(
                    HttpStatus.CONFLICT,
                    String.format(
                            "An account with email %s already exists.", accountRequestDto.email()));
        }
        Employee employee = new Employee();
        employee.setEmail(accountRequestDto.email());
        employee.setPassword(accountRequestDto.password());
        employee = employeeRepository.save(employee);
        AccountResponseDto dto = new AccountResponseDto(employee);
        return dto;
    }

    /**
     * Service method to create a new customer account.
     *
     * @param accountRequestDto AccountRequestDto (email, password).
     * @author Aurore Zhang (ororio0)
     */
    @Transactional
    public AccountResponseDto createCustomerAccount(@Valid AccountRequestDto accountRequestDto) {
        if (accountRepository.existsByEmail(accountRequestDto.email())) {
            throw new FashionStoreException(
                    HttpStatus.CONFLICT,
                    String.format(
                            "An account with email %s already exists.", accountRequestDto.email()));
        }
        Customer customer = new Customer();
        customer.setEmail(accountRequestDto.email());
        customer.setPassword(accountRequestDto.password());
        customer = customerRepository.save(customer);
        AccountResponseDto dto = new AccountResponseDto(customer);
        return dto;
    }
}
