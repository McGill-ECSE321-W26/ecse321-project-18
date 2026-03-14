package ca.mcgill.ecse321.fashionstore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.mcgill.ecse321.fashionstore.dto.OrderResponseDto;
import ca.mcgill.ecse321.fashionstore.dto.OrderStatusRequestDto;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.Order.State;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderRepository;
import java.sql.Date;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/** Integration tests for PUT /fashionstore/order/{id}/status */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureRestTestClient
public class OrderIntegrationTests {

    private static final String ORDER_STATUS_URI = "/fashionstore/order/{id}/status";
    private static final String ERROR_LOC = "$.errors";

    @Autowired private RestTestClient client;
    @Autowired private OrderRepository orderRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private EmployeeRepository employeeRepository;

    private int orderId;
    private int customerId;
    private int employeeId;
    private static final int INVALID_ORDER_ID = 99999;
    private static final int INVALID_EMPLOYEE_ID = 99999;

    /** Setup method — creates a customer, employee, and a purchased order in the database. */
    @BeforeAll
    public void setup() {
        Customer customer = createCustomer("customer@fashionstore.com", "password123");
        customerId = customer.getId();

        Employee employee = createEmployee("employee@fashionstore.com", "password123");
        employeeId = employee.getId();

        Order order =
                createOrder(
                        State.PURCHASED,
                        customer,
                        LocalDate.now(),
                        LocalDate.now().plusDays(10),
                        "67 Building Trottier",
                        123.45f);
        orderId = order.getId();
    }

    /** Cleanup method — deletes all test data from the database. */
    @AfterAll
    public void clearDatabase() {
        orderRepository.deleteAll();
        employeeRepository.deleteAll();
        customerRepository.deleteAll();
    }

    private Customer createCustomer(String email, String password) {
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword(password);
        return customerRepository.save(customer);
    }

    private Employee createEmployee(String email, String password) {
        Employee employee = new Employee();
        employee.setEmail(email);
        employee.setPassword(password);
        return employeeRepository.save(employee);
    }

    private Order createOrder(
            State state,
            Customer customer,
            LocalDate orderDate,
            LocalDate deliveryDate,
            String deliveryAddress,
            float price) {
        Order order = new Order();
        order.setState(state);
        order.setCustomer(customer);
        order.setOrderDate(Date.valueOf(orderDate));
        order.setDeliveryDate(Date.valueOf(deliveryDate));
        order.setDeliveryAddress(deliveryAddress);
        order.setPrice(price);
        return orderRepository.save(order);
    }

    /**
     * Integration test for updating order status to assigned with valid order and employee ID.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    @org.junit.jupiter.api.Order(0)
    void testUpdateOrderStatusToAssignedValid() {
        OrderStatusRequestDto body = new OrderStatusRequestDto(State.ASSIGNED, employeeId);

        OrderResponseDto response =
                client.put()
                        .uri(ORDER_STATUS_URI, orderId)
                        .body(body)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(OrderResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(response, "Response body should not be null.");
        assertEquals(State.ASSIGNED, response.state(), "Order state should be ASSIGNED.");
        assertEquals(employeeId, response.employeeId(), "Employee ID should match.");
    }

    /**
     * Integration test updating order status to prepared from assigned.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    @org.junit.jupiter.api.Order(1)
    void testUpdateOrderStatusToPreparedValid() {
        OrderStatusRequestDto body = new OrderStatusRequestDto(State.PREPARED, employeeId);

        OrderResponseDto response =
                client.put()
                        .uri(ORDER_STATUS_URI, orderId)
                        .body(body)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(OrderResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(response, "Response body should not be null.");
        assertEquals(State.PREPARED, response.state(), "Order state should be PREPARED.");
    }

    /**
     * Integration test for cancelling an order more than 24 hours before delivery.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    @org.junit.jupiter.api.Order(2)
    void testUpdateOrderStatusToCancelledValid() {
        OrderStatusRequestDto body = new OrderStatusRequestDto(State.CANCELLED, employeeId);

        OrderResponseDto response =
                client.put()
                        .uri(ORDER_STATUS_URI, orderId)
                        .body(body)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(OrderResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(response, "Response body should not be null.");
        assertEquals(State.CANCELLED, response.state(), "Order state should be CANCELLED.");
    }

    /**
     * Integration test for updating order status with an invalid order ID.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    @org.junit.jupiter.api.Order(3)
    void testUpdateOrderStatusInvalidOrderId() {
        OrderStatusRequestDto body = new OrderStatusRequestDto(State.ASSIGNED, employeeId);

        client.put()
                .uri(ORDER_STATUS_URI, INVALID_ORDER_ID)
                .body(body)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath(ERROR_LOC)
                .isEqualTo(String.format("Order ID %d was not found.", INVALID_ORDER_ID));
    }

    /**
     * Integration test for updating order status with an invalid employee ID.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    @org.junit.jupiter.api.Order(4)
    void testUpdateOrderStatusInvalidEmployeeId() {
        OrderStatusRequestDto body = new OrderStatusRequestDto(State.ASSIGNED, INVALID_EMPLOYEE_ID);

        client.put()
                .uri(ORDER_STATUS_URI, orderId)
                .body(body)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath(ERROR_LOC)
                .isEqualTo(String.format("Employee ID %d was not found.", INVALID_EMPLOYEE_ID));
    }

    /**
     * Integration test for an invalid state transition.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    @org.junit.jupiter.api.Order(5)
    void testUpdateOrderStatusInvalidTransition() {
        OrderStatusRequestDto body = new OrderStatusRequestDto(State.ASSIGNED, employeeId);

        client.put()
                .uri(ORDER_STATUS_URI, orderId)
                .body(body)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath(ERROR_LOC)
                .isEqualTo("Order must be in purchased state to be assigned.");
    }

    /**
     * Integration test for cancelling a delivered order.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    @org.junit.jupiter.api.Order(6)
    void testUpdateOrderStatusCancelDelivered() {
        int deliveredOrderId =
                createOrder(
                        State.DELIVERED,
                        null,
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        "67 Building Trottier",
                        50.00f).getId();
        OrderStatusRequestDto body = new OrderStatusRequestDto(State.CANCELLED, employeeId);

        client.put()
                .uri(ORDER_STATUS_URI, deliveredOrderId)
                .body(body)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath(ERROR_LOC)
                .isEqualTo("Cannot cancel an order that is delivered.");
    }

    /**
     * Integration test for cancelling an order less than 24 hours before delivery (invalid).
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    @org.junit.jupiter.api.Order(7)
    void testUpdateOrderStatusCancelTooLate() {
        int urgentOrderId =
                createOrder(
                        State.PURCHASED,
                        null,
                        LocalDate.now(),
                        LocalDate.now(),
                        "67 Building Trottier",
                        10.00f).getId();
        OrderStatusRequestDto body = new OrderStatusRequestDto(State.CANCELLED, employeeId);

        client.put()
                .uri(ORDER_STATUS_URI, urgentOrderId)
                .body(body)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath(ERROR_LOC)
                .isEqualTo("Order can only be cancelled at least 24 hours before the delivery date.");
    }
}