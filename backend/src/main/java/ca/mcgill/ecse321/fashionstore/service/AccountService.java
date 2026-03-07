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
     * @return An AccoutResponseDTO with the id, email and the account type (employee, customer,
     *     owner).
     * @throws FashionStoreException if an account with the email isn't found, or a password doesn't
     *     match
     * @author Qiuyu Huang (redacted24)
     */
    public AccountResponseDto accountLoginCheck(@Valid AccountRequestDto requestDto) {
        Account account = accountRepository.findAccountByEmail(requestDto.email());
        // Email check
        if (account == null) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST, "An account with that email does not exist.");
        }

        // Password check
        if (!account.getPassword().equals(requestDto.password())) {
            throw new FashionStoreException(HttpStatus.BAD_REQUEST, "Password is incorrect.");
        }

        // Account found. Check account type.
        return new AccountResponseDto(
                account.getId(), account.getEmail(), findAccountType(account.getId()));
    }

    /**
     * Method to return a string of the account type of a given account id.
     *
     * @param id The id of the account whose type we are trying to retrieve.
     * @return String "Owner"/"Employee"/"Customer"/"" depending on what type the account is
     */
    public String findAccountType(int id) {
        String accountType;
        Owner owner = ownerRepository.findOwnerById(id);
        Employee employee = employeeRepository.findEmployeeById(id);
        Customer customer = customerRepository.findCustomerById(id);
        if (owner != null) {
            accountType = "Owner";
        } else if (employee != null) {
            accountType = "Employee";
        } else if (customer != null) {
            accountType = "Customer";
        } else {
            accountType = "";
        }
        return accountType;
    }
}
