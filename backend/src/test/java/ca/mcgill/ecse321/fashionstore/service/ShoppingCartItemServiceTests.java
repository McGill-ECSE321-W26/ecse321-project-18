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
     * Helper fr service layer test for getting all of a customer's shopping cart items.
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
}
