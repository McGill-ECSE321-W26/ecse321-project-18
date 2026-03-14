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
        // Create customer
        Customer customer = new Customer();
        customer.setEmail("customer@fashionstore.com");
        customer.setPassword("password123");
        customer = customerRepository.save(customer);
        customerId = customer.getId();

        // Create employee
        Employee employee = new Employee();
        employee.setEmail("employee@fashionstore.com");
        employee.setPassword("password123");
        employee = employeeRepository.save(employee);
        employeeId = employee.getId();

        // Create a purchased order
        Order order = new Order();
        order.setState(State.PURCHASED);
        order.setCustomer(customer);
        order.setOrderDate(Date.valueOf(LocalDate.now()));
        order.setDeliveryDate(Date.valueOf(LocalDate.now().plusDays(10)));
        order.setDeliveryAddress("67 Building Trottier");
        order.setPrice(123.45f);
        order = orderRepository.save(order);
        orderId = order.getId();
    }

    /** Cleanup method — deletes all test data from the database. */
    @AfterAll
    public void clearDatabase() {
        orderRepository.deleteAll();
        employeeRepository.deleteAll();
        customerRepository.deleteAll();
    }

    /**
     * Integration test for updating order status to assigned with valid order and employee ID.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    @org.junit.jupiter.api.Order(0)
    void testUpdateOrderStatusToAssignedValid() {
        // Arrange
        OrderStatusRequestDto body = new OrderStatusRequestDto(State.ASSIGNED, employeeId);

        // Act
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

        // Assert
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
        // Arrange
        OrderStatusRequestDto body = new OrderStatusRequestDto(State.PREPARED, employeeId);

        // Act
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

        // Assert
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
        // Arrange
        OrderStatusRequestDto body = new OrderStatusRequestDto(State.CANCELLED, employeeId);

        // Act
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

        // Assert
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
        // Arrange
        OrderStatusRequestDto body = new OrderStatusRequestDto(State.ASSIGNED, employeeId);

        // Act + Assert
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
        // Arrange
        OrderStatusRequestDto body = new OrderStatusRequestDto(State.ASSIGNED, INVALID_EMPLOYEE_ID);

        // Act + Assert
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
        // Arrange
        OrderStatusRequestDto body = new OrderStatusRequestDto(State.ASSIGNED, employeeId);

        // Act + Assert
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
        // Arrange
        Order deliveredOrder = new Order();
        deliveredOrder.setState(State.DELIVERED);

        deliveredOrder.setOrderDate(Date.valueOf(LocalDate.now()));
        deliveredOrder.setDeliveryDate(Date.valueOf(LocalDate.now().plusDays(1)));
        deliveredOrder.setDeliveryAddress("67 Building Trottier");
        deliveredOrder.setPrice(50.00f);
        deliveredOrder = orderRepository.save(deliveredOrder);
        int deliveredOrderId = deliveredOrder.getId();

        OrderStatusRequestDto body = new OrderStatusRequestDto(State.CANCELLED, employeeId);

        // Act + Assert
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

        Order urgentOrder = new Order();
        urgentOrder.setState(State.PURCHASED);

        urgentOrder.setOrderDate(Date.valueOf(LocalDate.now()));
        urgentOrder.setDeliveryDate(Date.valueOf(LocalDate.now())); // delivery today
        urgentOrder.setDeliveryAddress("67 Building Trottier");
        urgentOrder.setPrice(10.00f);
        urgentOrder = orderRepository.save(urgentOrder);
        int urgentOrderId = urgentOrder.getId();

        OrderStatusRequestDto body = new OrderStatusRequestDto(State.CANCELLED, employeeId);

        client.put()
                .uri(ORDER_STATUS_URI, urgentOrderId)
                .body(body)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath(ERROR_LOC)
                .isEqualTo(
                        "Order can only be cancelled at least 24 hours before the delivery date.");
    }
}