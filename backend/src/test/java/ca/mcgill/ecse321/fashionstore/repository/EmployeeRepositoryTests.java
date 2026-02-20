package ca.mcgill.ecse321.fashionstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.mcgill.ecse321.fashionstore.model.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Aurore Zhang (ororio0)
 * @summary Test suite for employee persistence in the database.
 */
@SpringBootTest
class EmployeeRepositoryTests {

    @Autowired private EmployeeRepository employeeRepository;
    private Employee employee;

    @BeforeEach
    // create an employee
    void createEmployee() {
        String email = "employee@fashionstore.com";
        String password = "password123";
        String address = "123 Fashion Street";
        int numLoyaltyPoints = 50;

        Employee newEmployee = new Employee();
        newEmployee.setEmail(email);
        newEmployee.setPassword(password);
        newEmployee.setAddress(address);
        newEmployee.setNumLoyaltyPoints(numLoyaltyPoints);
        // save the employee
        employeeRepository.save(newEmployee);
        employee = newEmployee;
    }

    /**
     * Clears database after each test.
     *
     * @author Aurore Zhang (ororio0)
     */
    @AfterEach
    void clearDatabase() {
        employeeRepository.deleteAll();
    }

    /**
     * Test retrieval of employee from database is not null.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testPersistAndLoadEmployee() {
        // read from database
        Employee employeeFromDb = employeeRepository.findEmployeeByEmail(employee.getEmail());
        assertNotNull(employeeFromDb, "Could not find saved employee in database.");
    }

    /**
     * Test retrieval of employee email is correct.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testPersistAndLoadEmployeeEmail() {
        Employee employeeFromDb = employeeRepository.findEmployeeByEmail(employee.getEmail());
        assertEquals(
                employee.getEmail(),
                employeeFromDb.getEmail(),
                "Employee email is not saved in database.");
    }

    /**
     * Test retrieval of employee password is correct.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testPersistAndLoadEmployeePassword() {
        Employee employeeFromDb = employeeRepository.findEmployeeByEmail(employee.getEmail());
        assertEquals(
                employee.getPassword(),
                employeeFromDb.getPassword(),
                "Employee password is not saved in database.");
    }

    /**
     * Test retrieval of employee address is correct.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testPersistAndLoadEmployeeAddress() {
        Employee employeeFromDb = employeeRepository.findEmployeeByEmail(employee.getEmail());
        assertEquals(
                employee.getAddress(),
                employeeFromDb.getAddress(),
                "Employee address is not saved in database.");
    }

    /**
     * Test retrieval of employee loyalty points is correct.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testPersistAndLoadEmployeeNumLoyaltyPoints() {
        Employee employeeFromDb = employeeRepository.findEmployeeByEmail(employee.getEmail());
        assertEquals(
                employee.getNumLoyaltyPoints(),
                employeeFromDb.getNumLoyaltyPoints(),
                "Employee loyalty points are not saved in database.");
    }
}