package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.fashionstore.dto.OrderStatusRequestDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.Order.State;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

/** Order Service class tests. */
@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class OrderServiceTests {

    @Mock private OrderRepository orderRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private CustomerRepository customerRepository;

    @InjectMocks OrderService orderService;

    private static final int ORDER_ID = 1;
    private static final int CUSTOMER_ID = 10;
    private static final int EMPLOYEE_ID = 20;
    private static final int ANOTHER_EMPLOYEE_ID = 30;

    private Customer customer;
    private Employee employee;
    private Order purchasedOrder;

    /** Setup function for Order service layer tests. */
    @BeforeEach
    void setup() {
        customer = createCustomer(CUSTOMER_ID);
        employee = createEmployee(EMPLOYEE_ID);
        purchasedOrder = createOrder(ORDER_ID, State.PURCHASED, customer);
    }

    private Customer createCustomer(int id) {
        Customer c = new Customer();
        c.setId(id);
        return c;
    }

    private Employee createEmployee(int id) {
        Employee e = new Employee();
        e.setId(id);
        return e;
    }

    private Order createOrder(int id, State state, Customer customer) {
        Order o = new Order();
        o.setId(id);
        o.setState(state);
        o.setDeliveryDate(Date.valueOf(LocalDate.now().plusDays(10)));
        o.setOrderDate(Date.valueOf(LocalDate.now()));
        o.setDeliveryAddress("67 Building Trottier");
        o.setPrice(123.45f);
        o.setCustomer(customer);
        o.setEmployee(employee);
        return o;
    }

    /**
     * Service layer test for assigning an employee to an order in purchased state.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testUpdateOrderStatusToAssignedValid() {
        // Arrange
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(purchasedOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer((InvocationOnMock inv) -> (Order) inv.getArgument(0));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.ASSIGNED, EMPLOYEE_ID);

        // Act
        Order result = orderService.updateOrderStatus(ORDER_ID, dto);

        // Assert
        assertNotNull(result, "Order should not be null.");
        assertEquals(State.ASSIGNED, result.getState(), "Order state should be ASSIGNED after update.");
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /**
     * Service layer test for assigning an employee to an order that is NOT in purchased state
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testUpdateOrderStatusToAssignedFromInvalidState() {
        // Arrange
        Order assignedOrder = createOrder(ORDER_ID, State.ASSIGNED, customer);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(assignedOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.ASSIGNED, EMPLOYEE_ID);

        // Assert
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> orderService.updateOrderStatus(ORDER_ID, dto));

        assertEquals(
                HttpStatus.BAD_REQUEST,
                e.getStatus(),
                "HTTP status should be BAD_REQUEST when order is not in PURCHASED state.");
        assertEquals(
                "Order must be in purchased state to be assigned.",
                e.getMessage(),
                "Error message should indicate that order must be in PURCHASED state.");
    }

    /**
     * Service layer test for marking an order as prepared when it is in assigned state.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testUpdateOrderStatusToPreparedValid() {
        // Arrange
        Order assignedOrder = createOrder(ORDER_ID, State.ASSIGNED, customer);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(assignedOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer((InvocationOnMock inv) -> (Order) inv.getArgument(0));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.PREPARED, EMPLOYEE_ID);

        // Act
        Order result = orderService.updateOrderStatus(ORDER_ID, dto);

        // Assert
        assertNotNull(result, "Order should not be null.");
        assertEquals(State.PREPARED, result.getState(), "Order state should be PREPARED after update.");
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /**
     * Service layer test for marking an order as prepared when it is NOT in assigned state.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testUpdateOrderStatusToPreparedFromInvalidState() {
        // Arrange
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(purchasedOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.PREPARED, EMPLOYEE_ID);

        // Assert
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> orderService.updateOrderStatus(ORDER_ID, dto));

        assertEquals(
                HttpStatus.BAD_REQUEST,
                e.getStatus(),
                "HTTP status should be BAD_REQUEST when order is not in ASSIGNED state.");
        assertEquals(
                "Order must be in assigned state to be marked as prepared.",
                e.getMessage(),
                "Error message should indicate that order must be in ASSIGNED state.");
    }

    /**
     * Service layer test for cancelling an order that is more than 24 hours before delivery.
     *
     * @author Aurore Zhang
     */
    @Test
    void testUpdateOrderStatusToCancelledValid() {
        // Arrange
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(purchasedOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer((InvocationOnMock inv) -> (Order) inv.getArgument(0));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.CANCELLED, EMPLOYEE_ID);

        // Act
        Order result = orderService.updateOrderStatus(ORDER_ID, dto);

        // Assert
        assertNotNull(result, "Order should not be null.");
        assertEquals(State.CANCELLED, result.getState(), "Order state should be CANCELLED after update.");
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /**
     * Service layer test for cancelling an order that is already cancelled.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testUpdateOrderStatusToCancelledWhenAlreadyCancelled() {
        // Arrange
        Order cancelledOrder = createOrder(ORDER_ID, State.CANCELLED, customer);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(cancelledOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer((InvocationOnMock inv) -> (Order) inv.getArgument(0));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.CANCELLED, EMPLOYEE_ID);

        // Act
        Order result = orderService.updateOrderStatus(ORDER_ID, dto);

        // Assert
        assertNotNull(result, "Order should not be null.");
        assertEquals(State.CANCELLED, result.getState(), "Order state should remain CANCELLED.");
    }

    /**
     * Service layer test for cancelling a delivered order.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testUpdateOrderStatusToCancelledWhenDelivered() {
        // Arrange
        Order deliveredOrder = createOrder(ORDER_ID, State.DELIVERED, customer);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(deliveredOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.CANCELLED, EMPLOYEE_ID);

        // Assert
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> orderService.updateOrderStatus(ORDER_ID, dto));

        assertEquals(
                HttpStatus.BAD_REQUEST,
                e.getStatus(),
                "HTTP status should be BAD_REQUEST when trying to cancel a delivered order.");
        assertEquals(
                "Cannot cancel an order that is delivered.",
                e.getMessage(),
                "Error message should indicate that delivered orders cannot be cancelled.");
    }

    /**
     * Service layer test for cancelling an order less than 24 hours before delivery.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testUpdateOrderStatusToCancelledTooLate() {
        // Arrange
        Order urgentOrder = createOrder(ORDER_ID, State.PURCHASED, customer);
        urgentOrder.setDeliveryDate(Date.valueOf(LocalDate.now()));
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(urgentOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.CANCELLED, EMPLOYEE_ID);

        // Assert
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> orderService.updateOrderStatus(ORDER_ID, dto));

        assertEquals(
                HttpStatus.BAD_REQUEST,
                e.getStatus(),
                "HTTP status should be BAD_REQUEST when cancelling too close to delivery.");
        assertEquals(
                "Order can only be cancelled at least 24 hours before the delivery date.",
                e.getMessage(),
                "Error message should indicate the 24-hour cancellation rule.");
    }

    /**
     * Service layer test for transitioning to an invalid state.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testUpdateOrderStatusToInvalidTransition() {
        // Arrange — trying to set state to DELIVERED directly (not a valid transition)
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(purchasedOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.DELIVERED, EMPLOYEE_ID);

        // Assert
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> orderService.updateOrderStatus(ORDER_ID, dto));

        assertEquals(
                HttpStatus.BAD_REQUEST,
                e.getStatus(),
                "HTTP status should be BAD_REQUEST for an unsupported state transition.");
        assertEquals(
                String.format("Invalid status transition to %s.", State.DELIVERED),
                e.getMessage(),
                "Error message should indicate which state the invalid transition was to.");
    }

    /**
     * Service layer test for updating an order with an invalid employee ID.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testUpdateOrderStatusByInvalidOrderId() {
        // Arrange
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.ASSIGNED, EMPLOYEE_ID);

        // Assert
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> orderService.updateOrderStatus(ORDER_ID, dto));

        assertEquals(
                HttpStatus.NOT_FOUND,
                e.getStatus(),
                "HTTP status should be NOT_FOUND for an invalid order ID.");
    }
}