package ca.mcgill.ecse321.fashionstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.Order.State;
import java.sql.Date;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test suite for employee persistence in the database.
 *
 * @author Aurore Zhang (ororio0)
 */
@SpringBootTest
class EmployeeRepositoryTests {

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private OrderRepository orderRepository;
    private Employee employee;
    private Order order;

    /**
     * Creates and saves an employee before each test.
     *
     * @author Aurore Zhang (ororio0)
     */
    @BeforeEach
    void createEmployee() {
        String email = "ilovefashion@fashionstore.com";
        String password = "test-" + UUID.randomUUID();
        String address = " 12 Building McConnell";
        int numLoyaltyPoints = 8;
        // create and save Employee object
        Employee newEmployee = new Employee();
        newEmployee.setEmail(email);
        newEmployee.setPassword(password);
        newEmployee.setAddress(address);
        newEmployee.setNumLoyaltyPoints(numLoyaltyPoints);
        employeeRepository.save(newEmployee);
        employee = newEmployee;
        createOrder(newEmployee);
    }

    /**
     * Creates and saves an order assigned to the given employee.
     *
     * @author Aurore Zhang (ororio0)
     */
    private void createOrder(Employee newEmployee) {
        Order newOrder = new Order();
        newOrder.setState(State.ASSIGNED);
        newOrder.setOrderDate(Date.valueOf("2026-02-19"));
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

    /**
     * Test update of employee password is correct.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testUpdateEmployeePassword() {
        Employee employeeFromDb = employeeRepository.findEmployeeByEmail(employee.getEmail());
        employeeFromDb.setPassword("updated-" + UUID.randomUUID());
        employeeRepository.save(employeeFromDb);

        Employee updatedEmployee = employeeRepository.findEmployeeByEmail(employee.getEmail());
        assertEquals(
                employeeFromDb.getPassword(),
                updatedEmployee.getPassword(),
                "Employee password was not updated in database.");
    }

    /**
     * Test update of employee address is correct.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testUpdateEmployeeAddress() {
        Employee employeeFromDb = employeeRepository.findEmployeeByEmail(employee.getEmail());
        employeeFromDb.setAddress("99 Updated Street");
        employeeRepository.save(employeeFromDb);

        Employee updatedEmployee = employeeRepository.findEmployeeByEmail(employee.getEmail());
        assertEquals(
                employeeFromDb.getAddress(),
                updatedEmployee.getAddress(),
                "Employee address was not updated in database.");
    }

    /**
     * Test deletion of employee from database.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testDeleteEmployee() {
        orderRepository.removeEmployeeReference(employee.getEmail());
        employeeRepository.deleteAll();

        Employee deletedEmployee = employeeRepository.findEmployeeByEmail(employee.getEmail());
        assertNull(deletedEmployee, "Employee was not deleted from database.");

        Order remainingOrder = orderRepository.findOrderById(order.getId());
        assertNotNull(remainingOrder, "Order should still exist after employee is deleted.");
        assertNull(
                remainingOrder.getEmployee(),
                "Order should have no employee reference after employee is deleted.");
    }
}
