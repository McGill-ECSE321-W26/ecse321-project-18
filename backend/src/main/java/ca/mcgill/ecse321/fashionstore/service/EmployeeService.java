package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.AccountRequestDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.repository.AccountRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/** Employee Service class. */
@Service
@Validated
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final AccountRepository accountRepository;

    /**
     * EmployeeService constructor.
     *
     * @param employeeRepository EmployeeRepository required to access the database.
     * @param accountRepository AccountRepository required to check for duplicate emails.
     * @author Aurore Zhang (ororio0)
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public EmployeeService(
            EmployeeRepository employeeRepository, AccountRepository accountRepository) {
        this.employeeRepository = employeeRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Service method to create a new employee account.
     *
     * @param accountRequestDto Request DTO containing email and password.
     * @return The new created Employee instance.
     * @author Aurore Zhang (ororio0)
     */
    @Transactional
    public Employee createEmployee(@Valid AccountRequestDto accountRequestDto) {
        if (accountRepository.existsByEmail(accountRequestDto.email())) {
            throw new FashionStoreException(
                    HttpStatus.CONFLICT,
                    String.format(
                            "Account with email %s already exists.", accountRequestDto.email()));
        }
        Employee employee = new Employee();
        employee.setEmail(accountRequestDto.email());
        employee.setPassword(accountRequestDto.password());
        return employeeRepository.save(employee);
    }
}
