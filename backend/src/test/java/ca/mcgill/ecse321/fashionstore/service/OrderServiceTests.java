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
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.Order.State;
import ca.mcgill.ecse321.fashionstore.model.OrderItem;
import ca.mcgill.ecse321.fashionstore.model.ShoppingCartItem;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
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
    private static final int CLOTHING_ITEM_ID_1 = 5;
    private static final int CLOTHING_ITEM_ID_2 = 7;
    private static final int PRODUCT_ID = 50;
    private static final int SHOPPING_CART_ITEM_ID_1 = 3;
    private static final int SHOPPING_CART_ITEM_ID_2 = 4;
    private static final int QUANTITY_1 = 10;
    private static final int QUANTITY_2 = 20;
    private static final int ORDER_ITEM_ID_1 = 1;
    private static final int ORDER_ITEM_ID_2 = 2;
    private static final int CUSTOMER_ID_1 = 11;
    private static final int CUSTOMER_ID_2 = 14;
    private static final int ORDER_ID_1 = 1;
    private static final int ORDER_ID_2 = 2;

    private Customer customer;
    private Employee employee;
    private Order purchasedOrder;
    private ClothingProduct clothingProduct;
    private ClothingItem clothingItem1;
    private ClothingItem clothingItem2;
    private ShoppingCartItem shoppingCartItem1;
    private ShoppingCartItem shoppingCartItem2;
    private OrderItem orderItem1;
    private OrderItem orderItem2;
    private Customer customer1;
    private Customer customer2;
    private Order order1;
    private Order order2;

    /** Setup function for Order service layer tests. */
    @BeforeEach
    void setup() {
        customer = createCustomer(CUSTOMER_ID);
        employee = createEmployee(EMPLOYEE_ID);
        purchasedOrder = createOrder(ORDER_ID, State.PURCHASED, customer);
        clothingProduct = createClothingProduct(PRODUCT_ID);
        clothingItem1 = createClothingItem(CLOTHING_ITEM_ID_1, clothingProduct);
        clothingItem2 = createClothingItem(CLOTHING_ITEM_ID_2, clothingProduct);
        shoppingCartItem1 = createShoppingCartItem(SHOPPING_CART_ITEM_ID_1, QUANTITY_1, clothingItem1);
        shoppingCartItem2 = createShoppingCartItem(SHOPPING_CART_ITEM_ID_2, QUANTITY_2, clothingItem2);
        customer1 = createCustomerWithCart(CUSTOMER_ID_1, shoppingCartItem1);
        customer2 = createCustomerWithCart(CUSTOMER_ID_2, shoppingCartItem2);
        orderItem1 = createOrderItem(ORDER_ITEM_ID_1, shoppingCartItem1);
        orderItem2 = createOrderItem(ORDER_ITEM_ID_2, shoppingCartItem2);
        order1 = createOrderWithItem(ORDER_ID_1, customer1, orderItem1);
        order2 = createOrderWithItem(ORDER_ID_2, customer2, orderItem2);
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

    private ClothingProduct createClothingProduct(int id) {
        ClothingProduct newProduct = new ClothingProduct();
        newProduct.setId(id);
        return newProduct;
    }

    private ClothingItem createClothingItem(int id, ClothingProduct product) {
        ClothingItem newItem = new ClothingItem();
        newItem.setId(id);
        newItem.setClothingProduct(product);
        return newItem;
    }

    private ShoppingCartItem createShoppingCartItem(int id, int quantity, ClothingItem clothingItem) {
        ShoppingCartItem newItem = new ShoppingCartItem();
        newItem.setId(id);
        newItem.setQuantity(quantity);
        newItem.setClothingItem(clothingItem);
        return newItem;
    }

    private Customer createCustomerWithCart(int id, ShoppingCartItem shoppingCartItem) {
        Customer newCustomer = new Customer();
        newCustomer.setId(id);
        newCustomer.addShoppingCartItem(shoppingCartItem);
        return newCustomer;
    }

    private OrderItem createOrderItem(int id, ShoppingCartItem shoppingCartItem) {
        OrderItem newOrderItem = new OrderItem();
        newOrderItem.setId(id);
        newOrderItem.setClothingItem(shoppingCartItem.getClothingItem());
        newOrderItem.setQuantity(shoppingCartItem.getQuantity());
        newOrderItem.setPurchasePrice(
                shoppingCartItem.getClothingItem().getClothingProduct().getPrice());
        return newOrderItem;
    }

    private Order createOrderWithItem(int id, Customer customer, OrderItem orderItem) {
        Order newOrder = new Order();
        newOrder.setId(id);
        newOrder.setCustomer(customer);
        newOrder.addItem(orderItem);
        return newOrder;
    }

    private void assertGetOrder(Order order, int expectedId, int expectedCustomerId, int expectedSize) {
        assertEquals(expectedId, order.getId(), "Order does not have correct ID.");
        assertEquals(
                expectedCustomerId,
                order.getCustomer().getId(),
                "Order does not have correct customer ID.");
        assertEquals(
                expectedSize,
                order.getItems().size(),
                String.format("Order list of items does not have length %d.", expectedSize));
    }

    private void assertGetOrderItem(
            OrderItem item, int expectedId, int expectedQuantity, int expectedClothingItemId) {
        assertEquals(expectedId, item.getId(), "Order item does not have correct ID.");
        assertEquals(expectedQuantity, item.getQuantity(), "Order item does not have correct quantity.");
        assertEquals(
                expectedClothingItemId,
                item.getClothingItem().getId(),
                "Order item clothing item does not have correct ID.");
    }

    /**
     * Service layer test for assigning an employee to an order in purchased state.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testUpdateOrderStatusToAssignedValid() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(purchasedOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer((InvocationOnMock inv) -> (Order) inv.getArgument(0));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.ASSIGNED, EMPLOYEE_ID);

        Order result = orderService.updateOrderStatus(ORDER_ID, dto);

        assertNotNull(result, "Order should not be null.");
        assertEquals(State.ASSIGNED, result.getState(), "Order state should be ASSIGNED after update.");
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /**
     * Service layer test for assigning an employee to an order that is NOT in purchased state.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testUpdateOrderStatusToAssignedFromInvalidState() {
        Order assignedOrder = createOrder(ORDER_ID, State.ASSIGNED, customer);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(assignedOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.ASSIGNED, EMPLOYEE_ID);

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
        Order assignedOrder = createOrder(ORDER_ID, State.ASSIGNED, customer);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(assignedOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer((InvocationOnMock inv) -> (Order) inv.getArgument(0));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.PREPARED, EMPLOYEE_ID);

        Order result = orderService.updateOrderStatus(ORDER_ID, dto);

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
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(purchasedOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.PREPARED, EMPLOYEE_ID);

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
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(purchasedOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer((InvocationOnMock inv) -> (Order) inv.getArgument(0));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.CANCELLED, EMPLOYEE_ID);

        Order result = orderService.updateOrderStatus(ORDER_ID, dto);

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
        Order cancelledOrder = createOrder(ORDER_ID, State.CANCELLED, customer);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(cancelledOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer((InvocationOnMock inv) -> (Order) inv.getArgument(0));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.CANCELLED, EMPLOYEE_ID);

        Order result = orderService.updateOrderStatus(ORDER_ID, dto);

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
        Order deliveredOrder = createOrder(ORDER_ID, State.DELIVERED, customer);
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(deliveredOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.CANCELLED, EMPLOYEE_ID);

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
        Order urgentOrder = createOrder(ORDER_ID, State.PURCHASED, customer);
        urgentOrder.setDeliveryDate(Date.valueOf(LocalDate.now()));
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(urgentOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.CANCELLED, EMPLOYEE_ID);

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
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(purchasedOrder));
        when(employeeRepository.findById(EMPLOYEE_ID)).thenReturn(Optional.of(employee));

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.DELIVERED, EMPLOYEE_ID);

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
     * Service layer test for updating an order with an invalid order ID.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testUpdateOrderStatusByInvalidOrderId() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        OrderStatusRequestDto dto = new OrderStatusRequestDto(State.ASSIGNED, EMPLOYEE_ID);

        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> orderService.updateOrderStatus(ORDER_ID, dto));

        assertEquals(
                HttpStatus.NOT_FOUND,
                e.getStatus(),
                "HTTP status should be NOT_FOUND for an invalid order ID.");
    }

    /**
     * Service layer test for getting a list of orders in the system by valid customer id.
     *
     * @author Flavie Qin
     */
    @Test
    void testGetOrdersByValidId() {
        when(customerRepository.findById(CUSTOMER_ID_1)).thenReturn(Optional.of(customer1));

        List<Order> result = orderService.getAllOrdersByCustomer(CUSTOMER_ID_1);

        assertNotNull(result, "List of orders is null.");
        assertEquals(1, result.size(), "List of orders does not have length 1.");

        Order order = result.getFirst();
        assertGetOrder(order, ORDER_ID_1, CUSTOMER_ID_1, 1);

        OrderItem item = order.getItem(0);
        assertGetOrderItem(item, ORDER_ITEM_ID_1, QUANTITY_1, CLOTHING_ITEM_ID_1);

        verify(customerRepository, times(1)).findById(CUSTOMER_ID_1);
    }

    /**
     * Service layer test for getting all of a customer's orders with invalid ID.
     *
     * @author Flavie Qin
     */
    @Test
    void testGetShoppingCartItemsByInvalidId() {
        when(customerRepository.findById(CUSTOMER_ID_2)).thenReturn(Optional.empty());

        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> orderService.getAllOrdersByCustomer(CUSTOMER_ID_2));

        assertEquals(
                HttpStatus.NOT_FOUND,
                e.getStatus(),
                "HTTP status is not NOT_FOUND after invalid customer ID request.");
        assertEquals(
                String.format("Customer ID %d was not found.", CUSTOMER_ID_2),
                e.getMessage(),
                "HTTP message is not correct after invalid customer ID request.");
    }

    /**
     * Service layer test for getting a list of all orders in the system.
     *
     * @author Flavie Qin
     */
    @Test
    void testGetAllOrders() {
        when(orderRepository.findAll()).thenReturn(List.of(this.order1, this.order2));

        List<Order> result = orderService.getAllOrders();

        assertNotNull(result, "List of orders is null.");
        assertEquals(2, result.size(), "List of orders does not have length 2.");

        Order order1 = result.get(0);
        assertGetOrder(order1, ORDER_ID_1, CUSTOMER_ID_1, 1);
        OrderItem orderItem1 = order1.getItem(0);
        assertGetOrderItem(orderItem1, ORDER_ITEM_ID_1, QUANTITY_1, CLOTHING_ITEM_ID_1);

        Order order2 = result.get(1);
        assertGetOrder(order2, ORDER_ID_2, CUSTOMER_ID_2, 1);
        OrderItem orderItem2 = order2.getItem(0);
        assertGetOrderItem(orderItem2, ORDER_ITEM_ID_2, QUANTITY_2, CLOTHING_ITEM_ID_2);

        verify(orderRepository, times(1)).findAll();
    }
}