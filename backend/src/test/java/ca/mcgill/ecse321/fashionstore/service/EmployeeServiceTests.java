package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;

/** Employee Service class tests. */
@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class EmployeeServiceTests {
    @Mock private EmployeeRepository employeeRepository;

    @InjectMocks private EmployeeService employeeService;

    private static final int EMPLOYEE_ID = 5;
    private static final String EMPLOYEE_EMAIL = "employee@example.com";
    private static final String EMPLOYEE_ADDRESS = "42 Fashion Ave";
    private static final int EMPLOYEE_LOYALTY_PTS = 10;

    private Employee employee;

    @BeforeEach
    void setup() {
        employee = new Employee();
        employee.setId(EMPLOYEE_ID);
        employee.setEmail(EMPLOYEE_EMAIL);
        employee.setAddress(EMPLOYEE_ADDRESS);
        employee.setNumLoyaltyPoints(EMPLOYEE_LOYALTY_PTS);
    }

    @AfterEach
    void clearDatabase() {
        employeeRepository.deleteAll();
    }

    /**
     * Helper for: Test retrieving an existing employee.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    private void assertEmployeeDetails(Employee expected, Employee actual) {
        assertEquals(
                expected.getId(), actual.getId(), "Employee ID does not match expected value.");
        assertEquals(
                expected.getEmail(),
                actual.getEmail(),
                "Employee email does not match expected value.");
        assertEquals(
                expected.getAddress(),
                actual.getAddress(),
                "Employee address does not match expected value.");
        assertEquals(
                expected.getNumLoyaltyPoints(),
                actual.getNumLoyaltyPoints(),
                "Employee loyalty points do not match expected value.");
    }

    /**
     * Test retrieving an existing employee.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void testGetExistingEmployee() {
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));

        Employee response =
                assertDoesNotThrow(
                        () -> employeeService.getEmployee(EMPLOYEE_ID),
                        "Trying to get a valid, existing employee throws an exception.");

        assertEmployeeDetails(employee, response);
        verify(employeeRepository, times(1)).findById(EMPLOYEE_ID);
    }

    /**
     * Test retrieving a nonexistent employee.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void getNonExistentEmployee() {
        int id = employee.getId() + 28;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                FashionStoreException.class,
                () -> employeeService.getEmployee(id),
                "Trying to find non existent employee ID should not find anything.");
    }
}
