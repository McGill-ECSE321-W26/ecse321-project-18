package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.fashionstore.dto.ShoppingCartItemRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.ShoppingCartItemResponseDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.ShoppingCartItem;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.ShoppingCartItemRepository;
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

/** ShoppingCartItem Service class tests. */
@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class ShoppingCartItemServiceTests {
    @Mock private ShoppingCartItemRepository shoppingCartItemRepository;

    @Mock private ClothingItemRepository clothingItemRepository;

    @Mock private CustomerRepository customerRepository;

    @InjectMocks ShoppingCartItemService shoppingCartItemService;

    private static final int CLOTHING_ITEM_ID_1 = 5;
    private static final int CLOTHING_ITEM_ID_2 = 7;
    private static final int PRODUCT_ID = 50;
    private static final int SHOPPING_CART_ITEM_ID_1 = 3;
    private static final int SHOPPING_CART_ITEM_ID_2 = 4;
    private static final int QUANTITY_1 = 10;
    private static final int QUANTITY_2 = 20;
    private static final int CUSTOMER_ID = 11;

    private ClothingProduct clothingProduct;
    private ClothingItem clothingItem1;
    private ClothingItem clothingItem2;
    private ShoppingCartItem shoppingCartItem1;
    private ShoppingCartItem shoppingCartItem2;
    private Customer customer;

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
        List<ShoppingCartItem> shoppingCartItems = List.of(shoppingCartItem1, shoppingCartItem2);
        customer = createCustomer(CUSTOMER_ID, shoppingCartItems);
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

    private Customer createCustomer(int id, List<ShoppingCartItem> shoppingCartItems) {
        Customer newCustomer = new Customer();
        newCustomer.setId(id);
        for (ShoppingCartItem item : shoppingCartItems) {
            newCustomer.addShoppingCartItem(item);
        }
        return newCustomer;
    }

    /**
     * Service layer test for getting all of a customer's shopping cart items with valid ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testGetShoppingCartItemsByValidId() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

        // Act
        List<ShoppingCartItem> result = shoppingCartItemService.getShoppingCartItems(CUSTOMER_ID);

        // Assert
        assertNotNull(result, "List of shopping cart items is null.");
        assertEquals(2, result.size(), "List of shopping cart items does not have length 2.");
        assertGetShoppingCartItems(
                result.get(0), SHOPPING_CART_ITEM_ID_1, QUANTITY_1, CLOTHING_ITEM_ID_1);
        assertGetShoppingCartItems(
                result.get(1), SHOPPING_CART_ITEM_ID_2, QUANTITY_2, CLOTHING_ITEM_ID_2);
        verify(customerRepository, times(1)).findById(CUSTOMER_ID);
    }

    /**
     * Helper for service layer test for getting all of a customer's shopping cart items.
     *
     * @author Cyrus Fung (cfung89)
     */
    private void assertGetShoppingCartItems(
            ShoppingCartItem item, int expectedId, int expectedQuantity, int expectedClothingId) {
        // Assert
        assertEquals(expectedId, item.getId(), "Shopping cart item does not have correct ID.");
        assertEquals(
                expectedQuantity,
                item.getQuantity(),
                "Shopping cart item does not have correct quantity.");
        assertEquals(
                expectedClothingId,
                item.getClothingItem().getId(),
                "Shopping cart item does not have correct clothing item ID.");
        assertEquals(
                CUSTOMER_ID,
                item.getCustomer().getId(),
                "Shopping cart item does not have correct customer ID.");
    }

    /**
     * Service layer test for getting all of a customer's shopping cart items with invalid ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testGetShoppingCartItemsByInvalidId() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        // Assert
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> shoppingCartItemService.getShoppingCartItems(CUSTOMER_ID));

        assertEquals(
                HttpStatus.NOT_FOUND,
                e.getStatus(),
                "HTTP status is not NOT_FOUND after invalid customer ID request.");
        assertEquals(
                String.format("Customer ID %d was not found.", CUSTOMER_ID),
                e.getMessage(),
                "HTTP message is not correct after invalid customer ID request.");
    }

    /**
     * Helper for service layer test for creating and adding a shopping cart item.
     *
     * @author Cyrus Fung (cfung89)
     */
    private ShoppingCartItemResponseDto addShoppingCartItemSetup() {
        // Arrange
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(clothingItemRepository.findById(CLOTHING_ITEM_ID_2))
                .thenReturn(Optional.of(clothingItem2));
        when(shoppingCartItemRepository.save(any(ShoppingCartItem.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        // Act
        ShoppingCartItemRequestDto createdShoppingCartItem =
                new ShoppingCartItemRequestDto(CLOTHING_ITEM_ID_2, QUANTITY_2);
        ShoppingCartItem shoppingCartItem =
                shoppingCartItemService.addShoppingCartItem(CUSTOMER_ID, createdShoppingCartItem);

        return new ShoppingCartItemResponseDto(shoppingCartItem);
    }

    /**
     * Service layer test for creating and adding a shopping cart item.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testAddShoppingCartItem() {
        // Arrange and act
        ShoppingCartItemResponseDto shoppingCartItemResponseDto = addShoppingCartItemSetup();

        // Assert
        assertNotNull(shoppingCartItemResponseDto, "ShoppingCartItemResponseDto is null.");
        assertEquals(
                QUANTITY_2,
                shoppingCartItemResponseDto.quantity(),
                "ShoppingCartItemResponseDto does not contain correct quantity.");
        assertEquals(
                CUSTOMER_ID,
                shoppingCartItemResponseDto.customerId(),
                "ShoppingCartItemResponseDto does not contain correct Customer ID.");
        assertEquals(
                CLOTHING_ITEM_ID_2,
                shoppingCartItemResponseDto.clothingItem().id(),
                "ShoppingCartItemResponseDto does not contain correct ClothingItem ID.");
        verifyAddShoppingCartItem();
    }

    private void verifyAddShoppingCartItem() {
        verify(shoppingCartItemRepository, times(1))
                .save(
                        argThat(
                                (ShoppingCartItem item) ->
                                        item.getQuantity() == QUANTITY_2
                                                && item.getCustomer().getId() == CUSTOMER_ID
                                                && item.getClothingItem().getId()
                                                        == CLOTHING_ITEM_ID_2));
    }

    /**
     * Helper for service layer test for updating a shopping cart item.
     *
     * @author Cyrus Fung (cfung89)
     */
    private ShoppingCartItemResponseDto updateShoppingCartItemSetup() {
        // Arrange
        when(shoppingCartItemRepository.findById(SHOPPING_CART_ITEM_ID_1))
                .thenReturn(Optional.of(shoppingCartItem1));
        when(shoppingCartItemRepository.save(any(ShoppingCartItem.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        // Act
        ShoppingCartItemRequestDto updatedShoppingCartItem =
                new ShoppingCartItemRequestDto(CLOTHING_ITEM_ID_1, QUANTITY_2);
        ShoppingCartItem shoppingCartItem =
                shoppingCartItemService.updateShoppingCartItem(
                        SHOPPING_CART_ITEM_ID_1, updatedShoppingCartItem);

        return new ShoppingCartItemResponseDto(shoppingCartItem);
    }

    /**
     * Service layer test for updating a shopping cart items with valid ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testUpdateShoppingCartItemByValidId() {
        // Arrange and act
        ShoppingCartItemResponseDto shoppingCartItemResponseDto = updateShoppingCartItemSetup();

        // Assert
        assertNotNull(shoppingCartItemResponseDto, "ShoppingCartItemResponseDto is null.");
        assertEquals(
                QUANTITY_2,
                shoppingCartItemResponseDto.quantity(),
                "ShoppingCartItemResponseDto did not update the quantity correctly.");
        assertEquals(
                CUSTOMER_ID,
                shoppingCartItemResponseDto.customerId(),
                "ShoppingCartItemResponseDto does not contain correct Customer ID.");
        assertEquals(
                CLOTHING_ITEM_ID_1,
                shoppingCartItemResponseDto.clothingItem().id(),
                "ShoppingCartItemResponseDto does not contain correct ClothingItem ID.");
        verifyUpdateShoppingCartItemByValidId();
    }

    private void verifyUpdateShoppingCartItemByValidId() {
        verify(shoppingCartItemRepository, times(1))
                .save(
                        argThat(
                                (ShoppingCartItem item) ->
                                        item.getQuantity() == QUANTITY_2
                                                && item.getCustomer().getId() == CUSTOMER_ID
                                                && item.getClothingItem().getId()
                                                        == CLOTHING_ITEM_ID_1));
    }

    /**
     * Service layer test for updating a shopping cart items with invalid ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testUpdateShoppingCartItemByInvalidId() {
        when(shoppingCartItemRepository.findById(SHOPPING_CART_ITEM_ID_1))
                .thenReturn(Optional.empty());

        ShoppingCartItemRequestDto shoppingCartItemRequestDto =
                new ShoppingCartItemRequestDto(CLOTHING_ITEM_ID_1, QUANTITY_1);

        // Assert
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () ->
                                shoppingCartItemService.updateShoppingCartItem(
                                        SHOPPING_CART_ITEM_ID_1, shoppingCartItemRequestDto));

        assertEquals(
                HttpStatus.NOT_FOUND,
                e.getStatus(),
                "HTTP status is not NOT_FOUND after invalid shopping cart item ID request.");
        assertEquals(
                String.format("ShoppingCartItem ID %d was not found.", SHOPPING_CART_ITEM_ID_1),
                e.getMessage(),
                "HTTP message is not correct after invalid shopping cart item ID request.");
    }

    /**
     * Service layer test for deleting a shopping cart item.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testDeleteShoppingCartItem() {
        shoppingCartItemService.deleteShoppingCartItem(SHOPPING_CART_ITEM_ID_1);
        verify(shoppingCartItemRepository, times(1)).deleteById(SHOPPING_CART_ITEM_ID_1);
    }

    /**
     * Service layer test for deleting all a valid customer's shopping cart items.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testDeleteShoppingCartItemsByValidId() {
        // Arrange
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

        // Act
        shoppingCartItemService.deleteShoppingCartItems(CUSTOMER_ID);

        // Verify
        verify(shoppingCartItemRepository, times(2)).delete(any(ShoppingCartItem.class));
    }

    /**
     * Service layer test for deleting all an invalid customer's shopping cart items.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testDeleteShoppingCartItemsByInvalidId() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        // Assert
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> shoppingCartItemService.deleteShoppingCartItems(CUSTOMER_ID));

        assertEquals(
                HttpStatus.NOT_FOUND,
                e.getStatus(),
                "HTTP status is not NOT_FOUND after invalid customer ID request.");
        assertEquals(
                String.format("Customer ID %d was not found.", CUSTOMER_ID),
                e.getMessage(),
                "HTTP message is not correct after invalid customer ID request.");
    }
}
