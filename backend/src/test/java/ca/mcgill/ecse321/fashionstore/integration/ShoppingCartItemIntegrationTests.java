package ca.mcgill.ecse321.fashionstore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.mcgill.ecse321.fashionstore.dto.ShoppingCartItemRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.ShoppingCartItemResponseDto;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.ShoppingCartItem;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.ShoppingCartItemRepository;
import java.util.List;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;

/** ShoppingCartItem Service class tests. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureRestTestClient
class ShoppingCartItemIntegrationTests {
    private static final String responseNullError = "Response body is null.";
    private static final String shoppingCartItemUri =
            "/fashionstore/account/customer/{customerId}/shoppingcartitem";
    private static final String shoppingCartItemsUri =
            "/fashionstore/account/customer/{customerId}/shoppingcartitem/{id}";
    private static final String errorLoc = "$.errors";

    private static final int VALID_QUANTITY_1 = 10;
    private static final int VALID_QUANTITY_2 = 20;

    private int customerId;
    private int clothingItemId1;
    private int clothingItemId2;
    private int shoppingCartItemId1;
    private int shoppingCartItemId2;
    private int shoppingCartItemId3;

    @Autowired private RestTestClient client;

    @Autowired private ShoppingCartItemRepository shoppingCartItemRepository;
    @Autowired private ClothingItemRepository clothingItemRepository;
    @Autowired private ClothingProductRepository clothingProductRepository;
    @Autowired private CustomerRepository customerRepository;

    /** Setup method for ShoppingCartItem integration tests. */
    @BeforeAll
    public void setup() {
        // Arrange
        ClothingProduct clothingProduct = createClothingProduct();

        ClothingItem clothingItem1 = createClothingItem(clothingProduct);
        clothingItemId1 = clothingItem1.getId();

        ClothingItem clothingItem2 = createClothingItem(clothingProduct);
        clothingItemId2 = clothingItem2.getId();

        Customer customer = createCustomer();
        customerId = customer.getId();

        ShoppingCartItem shoppingCartItem =
                createShoppingCartItem(VALID_QUANTITY_1, clothingItem1, customer);
        shoppingCartItemId1 = shoppingCartItem.getId();
    }

    private ClothingProduct createClothingProduct() {
        ClothingProduct clothingProduct = new ClothingProduct();
        return clothingProductRepository.save(clothingProduct);
    }

    private ClothingItem createClothingItem(ClothingProduct clothingProduct) {
        ClothingItem clothingItem = new ClothingItem();
        clothingItem.setClothingProduct(clothingProduct);
        return clothingItemRepository.save(clothingItem);
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        return customerRepository.save(customer);
    }

    private ShoppingCartItem createShoppingCartItem(
            int quantity, ClothingItem clothingItem, Customer customer) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setQuantity(quantity);
        shoppingCartItem.setClothingItem(clothingItem);
        shoppingCartItem.setCustomer(customer);
        return shoppingCartItemRepository.save(shoppingCartItem);
    }

    /** Cleanup method for ShoppingCartItem integration tests. */
    @AfterAll
    public void clearDatabase() {
        shoppingCartItemRepository.deleteAll();
        customerRepository.deleteAll();
        clothingItemRepository.deleteAll();
    }

    private void assertBodyResponse(
            ShoppingCartItemRequestDto body, ShoppingCartItemResponseDto response) {
        assertEquals(
                body.clothingItemId(),
                response.clothingItem().id(),
                "ClothingItem ID of response is incorrect.");
        assertEquals(body.quantity(), response.quantity(), "Quantity of response is incorrect.");
        assertNotNull(response.customerId(), "Response customer ID is null.");
        assertEquals(customerId, response.customerId(), "Customer ID of response is incorrect.");
    }

    private void assertGetShoppingCartItemsByValidId(List<ShoppingCartItemResponseDto> response) {
        assertNotNull(response, responseNullError);
        assertEquals(response.size(), 2, "Response body has incorrect number of DTO objects.");
        assertGetShoppingCartItemsByValidId2(
                response.get(0), shoppingCartItemId1, VALID_QUANTITY_1, clothingItemId1);
        assertGetShoppingCartItemsByValidId2(
                response.get(1), shoppingCartItemId2, VALID_QUANTITY_2, clothingItemId2);
    }

    private void assertGetShoppingCartItemsByValidId2(
            ShoppingCartItemResponseDto item,
            int expectedId,
            int expectedQuantity,
            int expectedClothingId) {
        assertEquals(expectedId, item.id(), "Shopping cart item does not have correct ID.");
        assertEquals(
                expectedQuantity,
                item.quantity(),
                "Shopping cart item does not have correct quantity.");
        assertEquals(
                expectedClothingId,
                item.clothingItem().id(),
                "Shopping cart item does not have correct clothing item ID.");
        assertEquals(
                customerId,
                item.customerId(),
                "Shopping cart item does not have correct customer ID.");
    }

    /**
     * Integration test to add a shopping cart item with a valid ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(0)
    void testAddShoppingCartItemsByValidId() {
        // Arrange
        ShoppingCartItemRequestDto body =
                new ShoppingCartItemRequestDto(clothingItemId2, VALID_QUANTITY_2);

        // Act
        ShoppingCartItemResponseDto response =
                client.post()
                        .uri(shoppingCartItemUri, customerId)
                        .body(body)
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .expectBody(ShoppingCartItemResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertNotNull(response, responseNullError);
        assertTrue(response.id() > 0, "The ID should be a positive int.");
        this.shoppingCartItemId2 = response.id();
        assertBodyResponse(body, response);
    }

    /**
     * Integration test to get shopping cart items with a valid customer ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(1)
    void testGetShoppingCartItemsByValidId() {
        // Act
        List<ShoppingCartItemResponseDto> response =
                client.get()
                        .uri(shoppingCartItemUri, customerId)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(
                                new ParameterizedTypeReference<
                                        List<ShoppingCartItemResponseDto>>() {})
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertGetShoppingCartItemsByValidId(response);
    }

    /**
     * Integration test to update a shopping cart item with a valid ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(2)
    void testUpdateShoppingCartItemsByValidId() {
        // Arrange
        ShoppingCartItemRequestDto body =
                new ShoppingCartItemRequestDto(clothingItemId2, VALID_QUANTITY_1);

        // Act
        ShoppingCartItemResponseDto response =
                client.put()
                        .uri(shoppingCartItemsUri, customerId, shoppingCartItemId2)
                        .body(body)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(ShoppingCartItemResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertNotNull(response, responseNullError);
        assertTrue(response.id() > 0, "The ID should be a positive int.");
        assertEquals(response.id(), shoppingCartItemId2, "The ID is not the same.");
        assertBodyResponse(body, response);
    }

    /**
     * Integration test to add a shopping cart item with a valid ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(3)
    void testAddShoppingCartItemsByValidId2() {
        // Arrange
        ShoppingCartItemRequestDto body =
                new ShoppingCartItemRequestDto(clothingItemId2, VALID_QUANTITY_2);

        // Act
        ShoppingCartItemResponseDto response =
                client.post()
                        .uri(shoppingCartItemUri, customerId)
                        .body(body)
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .expectBody(ShoppingCartItemResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertNotNull(response, responseNullError);
        assertTrue(response.id() > 0, "The ID should be a positive int.");
        this.shoppingCartItemId3 = response.id();
        assertBodyResponse(body, response);
    }

    /**
     * Integration test to delete a shopping cart item with a valid ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(4)
    void testDeleteShoppingCartItemByValidId() {
        // Act
        client.delete()
                .uri(shoppingCartItemsUri, customerId, shoppingCartItemId3)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();

        // Assert
        assertFalse(
                shoppingCartItemRepository.existsById(shoppingCartItemId3),
                "Item 3 was not deleted from the database.");
    }

    /**
     * Integration test to delete all shopping cart items with a valid ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(5)
    void testDeleteShoppingCartItemsByValidId() {
        // Act
        client.delete()
                .uri(shoppingCartItemUri, customerId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();

        // Assert
        assertFalse(
                shoppingCartItemRepository.existsById(shoppingCartItemId1),
                "Item 1 was not deleted from the database.");
        assertFalse(
                shoppingCartItemRepository.existsById(shoppingCartItemId2),
                "Item 2 was not deleted from the database.");
        assertEquals(
                0,
                shoppingCartItemRepository.count(),
                "The shopping_cart_item table is not empty.");
    }

    /**
     * Integration test to get shopping cart items with a valid customer ID (empty response).
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(6)
    void testGetShoppingCartItemsByValidId2() {
        // Act
        List<ShoppingCartItemResponseDto> response =
                client.get()
                        .uri(shoppingCartItemUri, customerId)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(
                                new ParameterizedTypeReference<
                                        List<ShoppingCartItemResponseDto>>() {})
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertNotNull(response, responseNullError);
        assertEquals(response.size(), 0, "Response body has incorrect number of DTO objects.");
    }

    /**
     * Integration test to get shopping cart items with an invalid customer ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(6)
    void testGetShoppingCartItemsByInvalidId() {
        // Act
        client.get()
                .uri(shoppingCartItemUri, 40)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath(errorLoc)
                .isEqualTo("Customer ID 40 was not found.");
    }

    /**
     * Integration test to add shopping cart items with an invalid customer ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(6)
    void testAddShoppingCartItemsByInvalidId() {
        // Act
        ShoppingCartItemRequestDto body =
                new ShoppingCartItemRequestDto(clothingItemId2, VALID_QUANTITY_2);
        client.post()
                .uri(shoppingCartItemUri, 40)
                .body(body)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath(errorLoc)
                .isEqualTo("Customer ID 40 was not found.");
    }

    /**
     * Integration test to add shopping cart items with an invalid ClothingItem ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(6)
    void testAddShoppingCartItemsByInvalidId2() {
        // Act
        ShoppingCartItemRequestDto body = new ShoppingCartItemRequestDto(40, VALID_QUANTITY_2);
        client.post()
                .uri(shoppingCartItemUri, customerId)
                .body(body)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath(errorLoc)
                .isEqualTo("ClothingItem ID 40 was not found.");
    }

    /**
     * Integration test to update shopping cart items with an invalid ShoppingCartItem ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(6)
    void testUpdateShoppingCartItemsByInvalidId() {
        // Act
        ShoppingCartItemRequestDto body =
                new ShoppingCartItemRequestDto(clothingItemId1, VALID_QUANTITY_2);
        client.put()
                .uri(shoppingCartItemsUri, customerId, shoppingCartItemId1)
                .body(body)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath(errorLoc)
                .isEqualTo(
                        String.format(
                                "ShoppingCartItem ID %d was not found.", shoppingCartItemId1));
    }

    /**
     * Integration test to delete a shopping cart item that does not exist with a valid customer ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(6)
    void testDeleteShoppingCartItemByValidId2() {
        // Act
        client.delete()
                .uri(shoppingCartItemsUri, customerId, shoppingCartItemId1)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();
    }

    /**
     * Integration test to delete all shopping cart items that do not exist with a valid customer
     * ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(6)
    void testDeleteShoppingCartItemsByValidId2() {
        // Act
        client.delete()
                .uri(shoppingCartItemUri, customerId)
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();
    }

    /**
     * Integration test to delete all shopping cart items that with an invalid customer ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(6)
    void testDeleteShoppingCartItemsByInvalidId() {
        // Act
        client.delete()
                .uri(shoppingCartItemUri, 2)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath(errorLoc)
                .isEqualTo("Customer ID 2 was not found.");
    }
}
