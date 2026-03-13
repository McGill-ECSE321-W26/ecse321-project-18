package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.fashionstore.dto.AccountRequestDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.repository.AccountRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

/** Employee Service class tests. */
@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class EmployeeServiceTests {

    @Mock private EmployeeRepository employeeRepository;
    @Mock private AccountRepository accountRepository;

    @InjectMocks EmployeeService employeeService;

    private static final String VALID_EMAIL = "bestemployee@fashionstore.com";
    private static final String VALID_PASSWORD = "strongpassw0rd";
    private static final int EMPLOYEE_ID = 1;

    /**
     * Service layer test for creating an employee with a valid email and password.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testCreateEmployeeValid() {
        // Arrange
        when(accountRepository.existsByEmail(VALID_EMAIL)).thenReturn(false);
        when(employeeRepository.save(any(Employee.class)))
                .thenAnswer((InvocationOnMock inv) -> {
                    Employee e = (Employee) inv.getArgument(0);
                    e.setId(EMPLOYEE_ID);
                    return e;
                });

        AccountRequestDto dto = new AccountRequestDto(VALID_EMAIL, VALID_PASSWORD);

        // Act
        Employee result = employeeService.createEmployee(dto);

        // Assert
        assertNotNull(result, "Created employee should not be null.");
        assertEquals(VALID_EMAIL, result.getEmail(), "Employee should have the correct email.");
        assertEquals(VALID_PASSWORD, result.getPassword(), "Employee should have the correct password.");
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    /**
     * Service layer test for creating an employee with a duplicate email.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testCreateEmployeeDuplicateEmail() {
        // Arrange
        when(accountRepository.existsByEmail(VALID_EMAIL)).thenReturn(true);

        AccountRequestDto dto = new AccountRequestDto(VALID_EMAIL, VALID_PASSWORD);

        // Assert
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> employeeService.createEmployee(dto));

        assertEquals(
                HttpStatus.CONFLICT,
                e.getStatus(),
                "HTTP status should be CONFLICT when email already exists.");
        assertEquals(
                String.format("Account with email %s already exists.", VALID_EMAIL),
                e.getMessage(),
                "Error message should indicate the duplicate email.");
    }
}