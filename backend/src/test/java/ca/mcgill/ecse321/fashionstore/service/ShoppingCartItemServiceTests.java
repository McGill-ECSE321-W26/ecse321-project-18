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

    private static final int VALID_CUSTOMER_ID = 11;
    private static final int VALID_CLOTHING_ID_1 = 5;
    private static final int VALID_CLOTHING_ID_2 = 7;
    private static final int VALID_PRODUCT_ID = 50;
    private static final int VALID_ITEM_ID_1 = 3;
    private static final int VALID_ITEM_ID_2 = 4;
    private static final int VALID_QUANTITY_1 = 10;
    private static final int VALID_QUANTITY_2 = 20;

    /**
     * Helper for service layer test for getting all of a customer's shopping cart items.
     *
     * @author Cyrus Fung (cfung89)
     */
    private List<ShoppingCartItem> getShoppingCartItemsSetup() {
        // Arrange
        ClothingItem item1 = new ClothingItem();
        item1.setId(VALID_CLOTHING_ID_1);

        ClothingItem item2 = new ClothingItem();
        item2.setId(VALID_CLOTHING_ID_2);

        ShoppingCartItem cartItem1 = new ShoppingCartItem();
        cartItem1.setId(VALID_ITEM_ID_1);
        cartItem1.setClothingItem(item1);
        cartItem1.setQuantity(VALID_QUANTITY_1);

        ShoppingCartItem cartItem2 = new ShoppingCartItem();
        cartItem2.setId(VALID_ITEM_ID_2);
        cartItem2.setClothingItem(item2);
        cartItem2.setQuantity(VALID_QUANTITY_2);

        Customer customer = new Customer();
        customer.setId(VALID_CUSTOMER_ID);
        customer.addShoppingCartItem(cartItem1);
        customer.addShoppingCartItem(cartItem2);

        when(customerRepository.findById(VALID_CUSTOMER_ID)).thenReturn(Optional.of(customer));

        // Act
        return shoppingCartItemService.getShoppingCartItems(VALID_CUSTOMER_ID);
    }

    /**
     * Service layer test for getting all of a customer's shopping cart items with valid ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testGetShoppingCartItemsByValidId() {
        List<ShoppingCartItem> result = getShoppingCartItemsSetup();

        // Assert
        assertNotNull(result, "List of shopping cart items is null.");
        assertEquals(2, result.size(), "List of shopping cart items does not have length 2.");
        assertGetShoppingCartItems(
                result.get(0), VALID_ITEM_ID_1, VALID_QUANTITY_1, VALID_CLOTHING_ID_1);
        assertGetShoppingCartItems(
                result.get(1), VALID_ITEM_ID_2, VALID_QUANTITY_2, VALID_CLOTHING_ID_2);
        verify(customerRepository, times(1)).findById(VALID_CUSTOMER_ID);
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
                VALID_CUSTOMER_ID,
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
        when(customerRepository.findById(VALID_CUSTOMER_ID)).thenReturn(Optional.empty());

        // Assert
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> shoppingCartItemService.getShoppingCartItems(VALID_CUSTOMER_ID));

        assertEquals(
                HttpStatus.NOT_FOUND,
                e.getStatus(),
                "HTTP status is not NOT_FOUND after invalid customer ID request.");
        assertEquals(
                String.format("Customer ID %d was not found.", VALID_CUSTOMER_ID),
                e.getMessage(),
                "HTTP message is not correct after invalid customer ID request.");
    }

    /**
     * Helper for service layer test for creating and adding a shopping cart item.
     *
     * @author Cyrus Fung (cfung89)
     */
    private ShoppingCartItem addShoppingCartItemSetup() {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setId(VALID_CUSTOMER_ID);
        when(customerRepository.findById(VALID_CUSTOMER_ID)).thenReturn(Optional.of(newCustomer));

        ClothingProduct newProduct = new ClothingProduct();
        newProduct.setId(VALID_PRODUCT_ID);

        ClothingItem newClothingItem = new ClothingItem();
        newClothingItem.setId(VALID_CLOTHING_ID_2);
        newClothingItem.setClothingProduct(newProduct);
        when(clothingItemRepository.findById(VALID_CLOTHING_ID_2))
                .thenReturn(Optional.of(newClothingItem));

        when(shoppingCartItemRepository.save(any(ShoppingCartItem.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        // Act
        ShoppingCartItemRequestDto createdShoppingCartItem =
                new ShoppingCartItemRequestDto(VALID_CLOTHING_ID_2, VALID_QUANTITY_1);
        ShoppingCartItem shoppingCartItem =
                shoppingCartItemService.addShoppingCartItem(
                        VALID_CUSTOMER_ID, createdShoppingCartItem);
        return shoppingCartItem;
    }

    /**
     * Service layer test for creating and adding a shopping cart item.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testAddShoppingCartItem() {
        // Arrange and act
        ShoppingCartItemResponseDto shoppingCartItemResponseDto =
                new ShoppingCartItemResponseDto(addShoppingCartItemSetup());

        // Assert
        assertNotNull(shoppingCartItemResponseDto, "ShoppingCartItemResponseDto is null.");
        assertEquals(
                VALID_QUANTITY_1,
                shoppingCartItemResponseDto.quantity(),
                "ShoppingCartItemResponseDto does not contain correct quantity.");
        assertEquals(
                VALID_CUSTOMER_ID,
                shoppingCartItemResponseDto.customerId(),
                "ShoppingCartItemResponseDto does not contain correct Customer ID.");
        assertEquals(
                VALID_CLOTHING_ID_2,
                shoppingCartItemResponseDto.clothingItem().id(),
                "ShoppingCartItemResponseDto does not contain correct ClothingItem ID.");

        verify(shoppingCartItemRepository, times(1))
                .save(argThat((ShoppingCartItem item) -> VALID_QUANTITY_1 == item.getQuantity()));
    }

    /**
     * Helper for service layer test for updating a shopping cart item.
     *
     * @author Cyrus Fung (cfung89)
     */
    private ShoppingCartItem updateShoppingCartItemSetup() {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setId(VALID_CUSTOMER_ID);

        ClothingProduct newProduct = new ClothingProduct();
        newProduct.setId(VALID_PRODUCT_ID);

        ClothingItem newClothingItem = new ClothingItem();
        newClothingItem.setId(VALID_CLOTHING_ID_1);
        newClothingItem.setClothingProduct(newProduct);

        ShoppingCartItem newShoppingCartItem = new ShoppingCartItem();
        newShoppingCartItem.setId(VALID_ITEM_ID_1);
        newShoppingCartItem.setQuantity(VALID_QUANTITY_1);
        newShoppingCartItem.setCustomer(newCustomer);
        newShoppingCartItem.setClothingItem(newClothingItem);

        when(shoppingCartItemRepository.findById(VALID_ITEM_ID_1))
                .thenReturn(Optional.of(newShoppingCartItem));

        when(shoppingCartItemRepository.save(any(ShoppingCartItem.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        return updateShoppingCartItemAct();
    }

    /**
     * Helper for service layer test for updating a shopping cart item.
     *
     * @author Cyrus Fung (cfung89)
     */
    private ShoppingCartItem updateShoppingCartItemAct() {
        // Act
        ShoppingCartItemRequestDto updatedShoppingCartItem =
                new ShoppingCartItemRequestDto(VALID_CLOTHING_ID_1, VALID_QUANTITY_2);
        ShoppingCartItem shoppingCartItem =
                shoppingCartItemService.updateShoppingCartItem(
                        VALID_ITEM_ID_1, updatedShoppingCartItem);
        return shoppingCartItem;
    }

    /**
     * Service layer test for updating a shopping cart items with valid ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testUpdateShoppingCartItemByValidId() {
        // Arrange and act
        ShoppingCartItemResponseDto shoppingCartItemResponseDto =
                new ShoppingCartItemResponseDto(updateShoppingCartItemSetup());

        // Assert
        assertNotNull(shoppingCartItemResponseDto, "ShoppingCartItemResponseDto is null.");
        assertEquals(
                VALID_QUANTITY_2,
                shoppingCartItemResponseDto.quantity(),
                "ShoppingCartItemResponseDto did not update the quantity correctly.");
        assertEquals(
                VALID_CUSTOMER_ID,
                shoppingCartItemResponseDto.customerId(),
                "ShoppingCartItemResponseDto does not contain correct Customer ID.");
        assertEquals(
                VALID_CLOTHING_ID_1,
                shoppingCartItemResponseDto.clothingItem().id(),
                "ShoppingCartItemResponseDto does not contain correct ClothingItem ID.");

        verify(shoppingCartItemRepository, times(1))
                .save(argThat((ShoppingCartItem item) -> VALID_QUANTITY_2 == item.getQuantity()));
    }

    /**
     * Service layer test for updating a shopping cart items with invalid ID.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testUpdateShoppingCartItemByInvalidId() {
        when(shoppingCartItemRepository.findById(VALID_ITEM_ID_1)).thenReturn(Optional.empty());

        ShoppingCartItemRequestDto shoppingCartItemRequestDto =
                new ShoppingCartItemRequestDto(VALID_CLOTHING_ID_1, VALID_QUANTITY_1);

        // Assert
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () ->
                                shoppingCartItemService.updateShoppingCartItem(
                                        VALID_ITEM_ID_1, shoppingCartItemRequestDto));

        assertEquals(
                HttpStatus.NOT_FOUND,
                e.getStatus(),
                "HTTP status is not NOT_FOUND after invalid shopping cart item ID request.");
        assertEquals(
                String.format("ShoppingCartItem ID %d was not found.", VALID_ITEM_ID_1),
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
        shoppingCartItemService.deleteShoppingCartItem(VALID_ITEM_ID_1);
        verify(shoppingCartItemRepository, times(1)).deleteById(VALID_ITEM_ID_1);
    }

    /**
     * Service layer test for deleting all a valid customer's shopping cart items.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testDeleteShoppingCartItemsByValidId() {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setId(VALID_CUSTOMER_ID);

        ShoppingCartItem item1 = new ShoppingCartItem();
        item1.setId(VALID_ITEM_ID_1);

        ShoppingCartItem item2 = new ShoppingCartItem();
        item2.setId(VALID_ITEM_ID_2);

        newCustomer.addShoppingCartItem(item1);
        newCustomer.addShoppingCartItem(item2);

        when(customerRepository.findById(VALID_CUSTOMER_ID)).thenReturn(Optional.of(newCustomer));

        // Act
        shoppingCartItemService.deleteShoppingCartItems(VALID_CUSTOMER_ID);

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
        when(customerRepository.findById(VALID_CUSTOMER_ID)).thenReturn(Optional.empty());

        // Assert
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> shoppingCartItemService.deleteShoppingCartItems(VALID_CUSTOMER_ID));

        assertEquals(
                HttpStatus.NOT_FOUND,
                e.getStatus(),
                "HTTP status is not NOT_FOUND after invalid customer ID request.");
        assertEquals(
                String.format("Customer ID %d was not found.", VALID_CUSTOMER_ID),
                e.getMessage(),
                "HTTP message is not correct after invalid customer ID request.");
    }
}
