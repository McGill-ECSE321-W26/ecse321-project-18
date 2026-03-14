package ca.mcgill.ecse321.fashionstore.service;

import static ca.mcgill.ecse321.fashionstore.dto.AccountResponseDto.AccountType;

import ca.mcgill.ecse321.fashionstore.dto.AccountRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.AccountResponseDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Account;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.repository.AccountRepository;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OwnerRepository;
import jakarta.validation.Valid;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/** Account Service class. */
@Service
@Validated
public class AccountService {
    // error messages
    public static final String badPasswordErrorMsg = "Password is incorrect.";
    public static final String nonexistentEmailErrorMsg =
            "An account with that email does not exist.";

    private final AccountRepository accountRepository;
    private final OwnerRepository ownerRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;

    /**
     * AccountService constructor.
     *
     * @param accountRepository AccountRepository required to access the database.
     * @param ownerRepository OwnerRepository required to access the database.
     * @param customerRepository CustomerRepository required to access the database.
     * @param employeeRepository EmployeeRepository required to access the database.
     * @author Qiuyu Huang (redacted24)
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public AccountService(
            AccountRepository accountRepository,
            OwnerRepository ownerRepository,
            EmployeeRepository employeeRepository,
            CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.ownerRepository = ownerRepository;
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Checks if email exists and the password matches. If both are valid, user is authenticated and
     * granted access to the system. Otherwise, user is denied access and an error message is shown.
     *
     * @param requestDto An AccountRequestDto containing email and password.
     * @return An Account with the id, email and the account type (employee, customer, owner).
     * @throws FashionStoreException if an account with the email isn't found, or a password doesn't
     *     match
     * @author Qiuyu Huang (redacted24)
     */
    public Account accountLoginCheck(@Valid AccountRequestDto requestDto) {
        Account account = accountRepository.findAccountByEmail(requestDto.email());
        // Email check
        if (account == null) {
            throw new FashionStoreException(HttpStatus.BAD_REQUEST, nonexistentEmailErrorMsg);
        }

        // Password check
        if (!(account.getPassword().equals(requestDto.password()))) {
            throw new FashionStoreException(HttpStatus.BAD_REQUEST, badPasswordErrorMsg);
        }

        return account;
    }

    /**
     * Method to return a string of the account type of a given account id.
     *
     * @param id The id of the account whose type we are trying to retrieve.
     * @return AccountType (enum), depending on what the type of the account is
     *     (manager/owner/customer)
     * @author Qiuyu Huang (redacted24)
     */
    public AccountType findAccountType(int id) {
        if (ownerRepository.findOwnerById(id) != null) {
            return AccountType.OWNER;
        }
        if (employeeRepository.findEmployeeById(id) != null) {
            return AccountType.EMPLOYEE;
        }
        if (customerRepository.findCustomerById(id) != null) {
            return AccountType.CUSTOMER;
        }
        throw new FashionStoreException(HttpStatus.BAD_REQUEST, "Account not found.");
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
