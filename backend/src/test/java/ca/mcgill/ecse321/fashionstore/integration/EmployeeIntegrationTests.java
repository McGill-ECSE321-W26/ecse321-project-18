package ca.mcgill.ecse321.fashionstore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.mcgill.ecse321.fashionstore.dto.EmployeeResponseDto;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/** Employee Integration class tests. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureRestTestClient
class EmployeeIntegrationTests {
    private static final String EMPLOYEE_GET_URI = "/fashionstore/account/employee/{employeeId}";
    private static final String ERROR_LOC = "$.errors";
    private static final String RESPONSE_NULL_ERROR = "Response body is null.";

    private static final String EMPLOYEE_EMAIL = "employee@example.com";
    private static final String EMPLOYEE_ADDRESS = "42 Fashion Ave";
    private static final int EMPLOYEE_LOYALTY_PTS = 10;

    private int employeeId;

    @Autowired private RestTestClient client;

    @Autowired private EmployeeRepository employeeRepository;

    /** Setup method for Employee integration tests. */
    @BeforeAll
    public void setup() {
        Employee employee = new Employee();
        employee.setEmail(EMPLOYEE_EMAIL);
        employee.setAddress(EMPLOYEE_ADDRESS);
        employee.setNumLoyaltyPoints(EMPLOYEE_LOYALTY_PTS);
        employeeId = employeeRepository.save(employee).getId();
    }

    /** Cleanup method for Employee integration tests. */
    @AfterAll
    public void clearDatabase() {
        employeeRepository.deleteAll();
    }

    /**
     * Helper for: Integration test to retrieve an employee by a valid ID.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    private void assertEmployeeDetails(EmployeeResponseDto response) {
        assertEquals(employeeId, response.id(), "Employee ID does not match expected value.");
        assertEquals(
                EMPLOYEE_EMAIL, response.email(), "Employee email does not match expected value.");
        assertEquals(
                EMPLOYEE_ADDRESS,
                response.address(),
                "Employee address does not match expected value.");
        assertEquals(
                EMPLOYEE_LOYALTY_PTS,
                response.numOfLoyaltyPoints(),
                "Employee loyalty points do not match expected value.");

        assertNotNull(response.shoppingCartItems(), "Shopping cart items list should not be null.");
        assertNotNull(response.purchasedOrders(), "Purchased orders list should not be null.");
        assertNotNull(response.assignedOrders(), "Assigned orders list should not be null.");
    }

    /**
     * Integration test to retrieve an employee by a valid ID.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    @Order(0)
    void testGetEmployeeByValidId() {
        EmployeeResponseDto response =
                client.get()
                        .uri(EMPLOYEE_GET_URI, employeeId)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(EmployeeResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(response, RESPONSE_NULL_ERROR);
        assertEmployeeDetails(response);
    }

    /**
     * Integration test to retrieve an employee using an invalid ID.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    @Order(1)
    void testGetEmployeeByInvalidId() {
        int invalidEmployeeId = employeeId + 28;

        client.get()
                .uri(EMPLOYEE_GET_URI, invalidEmployeeId)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath(ERROR_LOC)
                .isEqualTo(String.format("Employee ID %d was not found.", invalidEmployeeId));
    }
}
