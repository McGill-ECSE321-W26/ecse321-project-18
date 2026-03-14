package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.OrderItem;
import ca.mcgill.ecse321.fashionstore.model.ShoppingCartItem;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

/** Order Service class tests. */
@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class OrderServiceTests {
    @Mock private OrderRepository orderRepository;

    @Mock private EmployeeRepository employeeRepository;

    @Mock private CustomerRepository customerRepository;

    @InjectMocks OrderService orderService;

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

    /** Setup function for ShoppingCartItem service layer tests. */
    @BeforeEach
    void setup() {
        // Arrange
        clothingProduct = createClothingProduct(PRODUCT_ID);
        clothingItem1 = createClothingItem(CLOTHING_ITEM_ID_1, clothingProduct);
        clothingItem2 = createClothingItem(CLOTHING_ITEM_ID_2, clothingProduct);
        shoppingCartItem1 =
                createShoppingCartItem(SHOPPING_CART_ITEM_ID_1, QUANTITY_1, clothingItem1);
        shoppingCartItem2 =
                createShoppingCartItem(SHOPPING_CART_ITEM_ID_2, QUANTITY_2, clothingItem2);
        customer1 = createCustomer(CUSTOMER_ID_1, shoppingCartItem1);
        customer2 = createCustomer(CUSTOMER_ID_2, shoppingCartItem2);
        orderItem1 = createOrderItem(ORDER_ITEM_ID_1, shoppingCartItem1);
        orderItem2 = createOrderItem(ORDER_ITEM_ID_2, shoppingCartItem2);
        order1 = createOrder(ORDER_ID_1, customer1, orderItem1);
        order2 = createOrder(ORDER_ID_2, customer2, orderItem2);
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

    private ShoppingCartItem createShoppingCartItem(
            int id, int quantity, ClothingItem clothingItem) {
        ShoppingCartItem newItem = new ShoppingCartItem();
        newItem.setId(id);
        newItem.setQuantity(quantity);
        newItem.setClothingItem(clothingItem);
        return newItem;
    }

    private Customer createCustomer(int id, ShoppingCartItem shoppingCartItem) {
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

    private Order createOrder(int id, Customer customer, OrderItem orderItem) {
        Order newOrder = new Order();
        newOrder.setId(id);
        newOrder.setCustomer(customer);
        newOrder.addItem(orderItem);
        return newOrder;
    }

    /**
     * Service layer test for getting a list of orders in the system by valid customer id.
     *
     * @author Flavie Qin
     */
    @Test
    void testGetOrdersByValidId() {
        when(customerRepository.findById(CUSTOMER_ID_1)).thenReturn(Optional.of(customer1));

        // Act
        List<Order> result = orderService.getAllOrdersByCustomer(CUSTOMER_ID_1);

        // Assert
        assertNotNull(result, "List of orders is null.");
        assertEquals(1, result.size(), "List of orders does not have length 1.");

        Order order = result.getFirst();
        assertGetOrder(order, ORDER_ID_1, CUSTOMER_ID_1, 1);

        OrderItem item = order.getItem(0);
        assertGetOrderItem(item, ORDER_ITEM_ID_1, QUANTITY_1, CLOTHING_ITEM_ID_1);

        verify(customerRepository, times(1)).findById(CUSTOMER_ID_1);
    }

    private void assertGetOrder(
            Order order, int expectedId, int expectedCustomerId, int expectedSize) {
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
        assertEquals(
                expectedQuantity, item.getQuantity(), "Order item does not have correct quantity.");
        assertEquals(
                expectedClothingItemId,
                item.getClothingItem().getId(),
                "Order item clothing item does not have correct ID.");
    }

    /**
     * Service layer test for getting all of a customer's orders with invalid ID.
     *
     * @author Flavie Qin
     */
    @Test
    void testGetShoppingCartItemsByInvalidId() {
        when(customerRepository.findById(CUSTOMER_ID_2)).thenReturn(Optional.empty());

        // Assert
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

        // Act
        List<Order> result = orderService.getAllOrders();

        // Assert
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
