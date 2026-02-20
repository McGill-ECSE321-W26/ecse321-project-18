package ca.mcgill.ecse321.fashionstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.Order.State;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.sql.Date;

/**
 * @author Aurore Zhang (ororio0)
 * @summary Test suite for employee persistence in the database.
 */
@SpringBootTest
class EmployeeRepositoryTests {

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private OrderRepository orderRepository;
    private Employee employee;
    private Order order;

    @BeforeEach
    // create an employee
    void createEmployee() {
        String email = "ilovefashion@fashionstore.com";
        String password = "P@ssw0rd";
        String address = " 12 Building McConnell";
        int numLoyaltyPoints = 8;

        Employee newEmployee = new Employee();
        newEmployee.setEmail(email);
        newEmployee.setPassword(password);
        newEmployee.setAddress(address);
        newEmployee.setNumLoyaltyPoints(numLoyaltyPoints);
        // save the employee
        employeeRepository.save(newEmployee);
        employee = newEmployee;

        // create an order and assign to employee
        Order newOrder = new Order();
        newOrder.setState(State.ASSIGNED);
        newOrder.setOrderDate(Date.valueOf("2030-02-30"));
        newOrder.setDeliveryAddress("67 McGill University Avenue");
        newOrder.setPrice(67.88f);
        newOrder.setEmployee(newEmployee);
        orderRepository.save(newOrder);
        order = newOrder;
    }

    /**
     * Clears database after each test.
     *
     * @author Aurore Zhang (ororio0)
     */
    @AfterEach
    void clearDatabase() {
        orderRepository.deleteAll();
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
    /**
     * Test retrieval of employee's assigned order reference is correct.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testPersistAndLoadEmployeeAssignedOrder() {
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertNotNull(orderFromDb, "Could not find saved order in database.");
        assertNotNull(orderFromDb.getEmployee(), "Order has no employee reference in database.");
        assertEquals(
                employee.getEmail(),
                orderFromDb.getEmployee().getEmail(),
                "Employee reference in order is not saved correctly in database.");
    }
}