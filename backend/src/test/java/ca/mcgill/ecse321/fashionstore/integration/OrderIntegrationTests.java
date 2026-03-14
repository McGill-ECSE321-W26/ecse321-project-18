package ca.mcgill.ecse321.fashionstore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.mcgill.ecse321.fashionstore.dto.OrderItemResponseDto;
import ca.mcgill.ecse321.fashionstore.dto.OrderRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.OrderResponseDto;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.OrderItem;
import ca.mcgill.ecse321.fashionstore.model.ShoppingCartItem;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderRepository;
import ca.mcgill.ecse321.fashionstore.repository.ShoppingCartItemRepository;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

/** ShoppingCartItem Service class tests. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureRestTestClient
class OrderIntegrationTests {
    private static final String responseNullError = "Response body is null.";
    private static final String customerOrderUri =
            "/fashionstore/account/customer/{customerId}/order";
    private static final String orderUri = "/fashionstore/order";
    private static final String errorLoc = "$.errors";

    private static final int VALID_QUANTITY_1 = 10;
    private static final int VALID_QUANTITY_2 = 20;
    private static final int INVALID_QUANTITY = 60;
    private static final int CLOTHING_ITEM_1_QUANTITY = 50;
    private static final int CLOTHING_ITEM_2_QUANTITY = 20;

    private static final float PRICE = 23.99f;
    private static final LocalDate ORDER_DATE = LocalDate.now();
    private static final LocalDate VALID_DELIVERY_DATE = LocalDate.now().plusDays(2);
    private static final LocalDate INVALID_DELIVERY_DATE = LocalDate.now();
    private static final String ADDRESS = "123 Solid State Drive";

    private Customer customer1; // one order and valid shopping cart
    private Customer customer2; // one order and valid shopping cart
    private Customer customer3; // no order and invalid shopping cart quantity
    private Customer customer4; // no order and no shopping cart
    private ClothingItem clothingItem1;
    private ClothingItem clothingItem2;
    private ShoppingCartItem shoppingCartItem1;
    private ShoppingCartItem shoppingCartItem2;
    private Order order1;
    private OrderItem orderItem1;
    private Order order2;
    private OrderItem orderItem2;

    @Autowired private RestTestClient client;

    @Autowired private CustomerRepository customerRepository;
    @Autowired private ClothingItemRepository clothingItemRepository;
    @Autowired private ClothingProductRepository clothingProductRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private ShoppingCartItemRepository shoppingCartItemRepository;
    @Autowired private OrderItemRepository orderItemRepository;

    /** Setup method for Order integration tests. */
    @BeforeAll
    public void setup() {
        // Arrange
        ClothingProduct clothingProduct = createClothingProduct();
        clothingItem1 = createClothingItem(clothingProduct, CLOTHING_ITEM_1_QUANTITY);
        clothingItem2 = createClothingItem(clothingProduct, CLOTHING_ITEM_2_QUANTITY);

        customer1 = createCustomer("customer1@email.com", "hihihaha");
        customer2 = createCustomer("customer2@email.com", "helloworld");
        customer3 = createCustomer("customer3@email.com", "hohohoho");
        customer4 = createCustomer("customer4@email.com", "huuuuuuuu");

        shoppingCartItem1 = createShoppingCartItem(VALID_QUANTITY_1, clothingItem1, customer1);
        order1 = createOrder(customer1);
        orderItem1 = createOrderItem(order1, shoppingCartItem1);

        shoppingCartItem2 = createShoppingCartItem(VALID_QUANTITY_2, clothingItem2, customer2);
        order2 = createOrder(customer2);
        orderItem2 = createOrderItem(order2, shoppingCartItem2);

        createShoppingCartItem(INVALID_QUANTITY, clothingItem2, customer3);
    }

    private Customer createCustomer(String email, String password) {
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword(password);
        return customerRepository.save(customer);
    }

    private ClothingProduct createClothingProduct() {
        ClothingProduct clothingProduct = new ClothingProduct();
        clothingProduct.setName("T-shirt");
        clothingProduct.setPrice(25.99f);
        return clothingProductRepository.save(clothingProduct);
    }

    private ClothingItem createClothingItem(ClothingProduct clothingProduct, int numInStock) {
        ClothingItem clothingItem = new ClothingItem();
        clothingItem.setClothingProduct(clothingProduct);
        clothingItem.setNumInStock(numInStock);
        return clothingItemRepository.save(clothingItem);
    }

    private ShoppingCartItem createShoppingCartItem(
            int quantity, ClothingItem clothingItem, Customer customer) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setQuantity(quantity);
        shoppingCartItem.setClothingItem(clothingItem);
        shoppingCartItem.setCustomer(customer);
        return shoppingCartItemRepository.save(shoppingCartItem);
    }

    private Order createOrder(Customer customer) {
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(Date.valueOf(LocalDate.now()));
        order.setDeliveryDate(Date.valueOf(LocalDate.now().plusDays(3)));
        order.setState(Order.State.PURCHASED);
        order.setPrice(23.99f);
        return orderRepository.save(order);
    }

    private OrderItem createOrderItem(Order order, ShoppingCartItem shoppingCartItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setPurchasePrice(
                shoppingCartItem.getClothingItem().getClothingProduct().getPrice());
        orderItem.setQuantity(shoppingCartItem.getQuantity());
        orderItem.setClothingItem(shoppingCartItem.getClothingItem());
        return orderItemRepository.save(orderItem);
    }

    /** Cleanup method for Order integration tests. */
    @AfterAll
    public void clearDatabase() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        shoppingCartItemRepository.deleteAll();
        clothingItemRepository.deleteAll();
        clothingProductRepository.deleteAll();
        employeeRepository.deleteAll();
        customerRepository.deleteAll();
    }

    /**
     * Integration test to get all orders from a valid customer id.
     *
     * @author Flavie Qin
     */
    @Test
    @org.junit.jupiter.api.Order(0)
    void testGetAllOrdersByValidId() {
        // Act
        List<OrderResponseDto> response =
                client.get()
                        .uri(customerOrderUri, customer1.getId())
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(new ParameterizedTypeReference<List<OrderResponseDto>>() {})
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertNotNull(response, responseNullError);
        assertEquals(1, response.size(), "Response body has incorrect number of DTO objects.");
        OrderResponseDto orderResponseDto = response.getFirst();
        assertOrderResponseDto(order1, orderResponseDto, orderItem1);
    }

    private void assertOrderResponseDto(
            Order order, OrderResponseDto orderResponseDto, OrderItem orderItem) {
        assertEquals(
                order.getId(), orderResponseDto.id(), "ID of order response DTO is incorrect.");
        assertEquals(
                order.getCustomer().getId(),
                orderResponseDto.customerId(),
                "Customer ID of order response DTO is incorrect.");
        assertOrderResponseDtoFields(order, orderResponseDto);
        assertEquals(
                1,
                orderResponseDto.orderItems().size(),
                "Order items does not have correct length in response.");
        assertOrderItemResponseDto(orderItem, orderResponseDto.orderItems().getFirst());
    }

    private void assertOrderResponseDtoFields(Order order, OrderResponseDto orderResponseDto) {
        assertEquals(
                order.getPrice(),
                orderResponseDto.price(),
                "Price of order response DTO is incorrect.");
        assertEquals(
                order.getDeliveryAddress(),
                orderResponseDto.deliveryAddress(),
                "Price of order response DTO is incorrect.");
        assertEquals(
                order.getOrderDate(),
                Date.valueOf(orderResponseDto.orderDate()),
                "Order date of order response DTO is incorrect.");
        assertEquals(
                order.getDeliveryDate(),
                Date.valueOf(orderResponseDto.deliveryDate()),
                "Delivery date of order response DTO is incorrect.");
    }

    private void assertOrderItemResponseDto(
            OrderItem orderItem, OrderItemResponseDto orderItemResponseDto) {
        assertEquals(
                orderItem.getId(),
                orderItemResponseDto.orderId(),
                "Order item in order response DTO is incorrect.");
        assertEquals(
                orderItem.getClothingItem().getId(),
                orderItemResponseDto.clothingItem().id(),
                "Order item clothing item id in order response DTO is incorrect.");
        assertEquals(
                orderItem.getQuantity(),
                orderItemResponseDto.quantity(),
                "Order item quantity in order response DTO is incorrect.");
    }

    /**
     * Integration test to get all orders in the system.
     *
     * @author Flavie Qin
     */
    @Test
    @org.junit.jupiter.api.Order(1)
    void testGetAllOrders() {
        // Act
        List<OrderResponseDto> response =
                client.get()
                        .uri(orderUri)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(new ParameterizedTypeReference<List<OrderResponseDto>>() {})
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertNotNull(response, responseNullError);
        assertEquals(2, response.size(), "Response body has incorrect number of DTO objects.");
        response.sort(Comparator.comparing(OrderResponseDto::id));
        OrderResponseDto orderResponseDto1 = response.getFirst();
        OrderResponseDto orderResponseDto2 = response.get(1);
        assertOrderResponseDto(order1, orderResponseDto1, orderItem1);
        assertOrderResponseDto(order2, orderResponseDto2, orderItem2);
    }

    /**
     * Integration test to get all orders from a valid customer id with no orders.
     *
     * @author Flavie Qin
     */
    @Test
    @org.junit.jupiter.api.Order(2)
    void testGetAllOrdersByValidIdNoOrders() {
        // Act
        List<OrderResponseDto> response =
                client.get()
                        .uri(customerOrderUri, customer4.getId())
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(new ParameterizedTypeReference<List<OrderResponseDto>>() {})
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertNotNull(response, responseNullError);
        assertEquals(0, response.size(), "Response body has incorrect number of DTO objects.");
    }

    /**
     * Integration test to get all orders of an invalid customer ID.
     *
     * @author Flavie Qin
     */
    @Test
    @org.junit.jupiter.api.Order(3)
    void testGetAllOrdersByInvalidId() {
        // Act and assert
        client.get()
                .uri(customerOrderUri, 40)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath(errorLoc)
                .isEqualTo("Customer ID 40 was not found.");
    }

    private OrderRequestDto createValidOrderRequest() {
        return new OrderRequestDto(
                Order.State.PURCHASED, ORDER_DATE, VALID_DELIVERY_DATE, ADDRESS, PRICE);
    }

    private OrderRequestDto createInvalidOrderRequest() {
        return new OrderRequestDto(
                Order.State.PURCHASED, ORDER_DATE, INVALID_DELIVERY_DATE, ADDRESS, PRICE);
    }

    /**
     * Integration test to create a new, valid order
     *
     * @author Flavie Qin
     */
    @Test
    @org.junit.jupiter.api.Order(4)
    void testCreateOrderValid() {
        // Arrange
        OrderRequestDto request = createValidOrderRequest();

        // Act
        OrderResponseDto response =
                client.post()
                        .uri(customerOrderUri, customer1.getId())
                        .body(request)
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .expectBody(OrderResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertNotNull(response, responseNullError);
        assertTrue(response.id() >= 0, "The ID should be a positive int.");
        assertNewOrderResponseDto(request, response);
    }

    private void assertNewOrderResponseDto(OrderRequestDto request, OrderResponseDto response) {
        assertEquals(
                request.deliveryAddress(),
                response.deliveryAddress(),
                "Response delivery address is incorrect.");
        assertEquals(
                request.orderDate(), response.orderDate(), "Response order date is incorrect.");
        assertEquals(
                request.deliveryDate(),
                response.deliveryDate(),
                "Response delivery date is incorrect.");
        assertEquals(request.state(), response.state(), "Response state is incorrect.");
        assertEquals(request.price(), response.price(), "Response price is incorrect.");
        assertEquals(1, response.orderItems().size(), "Response order items length is incorrect");
        assertNewOrderItemsResponseDto(response.orderItems().getFirst());
    }

    private void assertNewOrderItemsResponseDto(OrderItemResponseDto orderItem) {
        assertTrue(orderItem.id() >= 0, "The ID should be a positive int.");
        assertEquals(
                clothingItem1.getId(),
                orderItem.clothingItem().id(),
                "Order item clothing item id in order response DTO is incorrect.");
        assertEquals(
                shoppingCartItem1.getQuantity(),
                orderItem.quantity(),
                "Order item quantity in order response DTO is incorrect.");
        assertEquals(
                CLOTHING_ITEM_1_QUANTITY - VALID_QUANTITY_1,
                orderItem.clothingItem().numInStock(),
                "Clothing item num in stock was not updated correctly.");
    }

    /**
     * Integration test to create order with an invalid customer id
     *
     * @author Flavie Qin
     */
    @Test
    @org.junit.jupiter.api.Order(5)
    void testCreateOrderByInvalidId() {
        // Arrange
        OrderRequestDto request = createValidOrderRequest();
        // Act and assert
        client.post()
                .uri(customerOrderUri, 40)
                .body(request)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath(errorLoc)
                .isEqualTo("Customer ID 40 was not found.");
    }

    /**
     * Integration test to create order with an invalid OrderRequestDto
     *
     * @author Flavie Qin
     */
    @Test
    @org.junit.jupiter.api.Order(6)
    void testCreateOrderByInvalidRequestDates() {
        OrderRequestDto request = createInvalidOrderRequest();

        // Act and assert
        client.post()
                .uri(customerOrderUri, customer2.getId())
                .body(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath(errorLoc)
                .isEqualTo("Delivery date must be at least 24 hours after order date.");
    }

    /**
     * Integration test to create order with an invalid shopping cart (empty)
     *
     * @author Flavie Qin
     */
    @Test
    @org.junit.jupiter.api.Order(7)
    void testCreateOrderByInvalidShoppingCart() {
        // Arrange
        OrderRequestDto request = createValidOrderRequest();

        // Act and assert
        client.post()
                .uri(customerOrderUri, customer4.getId())
                .body(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath(errorLoc)
                .isEqualTo("Cannot create a new order for no items.");
    }

    /**
     * Integration test to create order with an invalid quantity in shopping cart of customer
     *
     * @author Flavie Qin
     */
    @Test
    @org.junit.jupiter.api.Order(8)
    void testCreateOrderByInvalidQuantity() {
        // Arrange
        OrderRequestDto request = createValidOrderRequest();

        // Act and assert
        client.post()
                .uri(customerOrderUri, customer3.getId())
                .body(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath(errorLoc)
                .isEqualTo("Clothing item T-shirt does not have enough quantity in stock.");

        assertEquals(
                CLOTHING_ITEM_2_QUANTITY,
                clothingItem2.getNumInStock(),
                "Clothing item num in stock was incorrectly updated.");
    }
}
