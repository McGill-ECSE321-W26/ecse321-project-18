package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.fashionstore.dto.ClothingItemRequestDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

/**
 * Test suite for clothing item service class.
 *
 * @author Qiuyu Huang (redacted24)
 */
@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class ClothingItemServiceTests {
    @Mock private ClothingProductRepository clothingProductRepository;
    @Mock private ClothingItemRepository clothingItemRepository;

    @InjectMocks private ClothingItemService clothingItemService;

    private ClothingProduct clothingProduct;
    private ClothingItem clothingItem;
    private ClothingItemRequestDto validClothingItemRequestDto;
    private ClothingItemRequestDto invalidClothingItemRequestDto;

    /**
     * Creates and saves a clothing item.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @BeforeEach
    void createClothingItems() {
        // set clothing product first
        ClothingProduct clothingProduct = new ClothingProduct();
        clothingProduct.setImage("hoodie.png");
        clothingProduct.setPrice(69.99f);
        clothingProduct.setName("Hoodies");
        this.clothingProduct = clothingProduct;

        // create clothing item
        ClothingItem clothingItem = new ClothingItem();
        clothingItem.setClothingProduct(clothingProduct);
        clothingItem.setSize(ClothingItem.Size.M);
        clothingItem.setColour(ClothingItem.Colour.YELLOW);
        clothingItem.setNumInStock(100);
        this.clothingItem = clothingItem;

        createClothingItemDtos();
    }

    private void createClothingItemDtos() {
        this.validClothingItemRequestDto =
                new ClothingItemRequestDto(
                        ClothingItem.Size.S,
                        ClothingItem.Colour.BLACK,
                        50,
                        this.clothingProduct.getId());
        this.invalidClothingItemRequestDto =
                new ClothingItemRequestDto(
                        ClothingItem.Size.S,
                        ClothingItem.Colour.BLACK,
                        50,
                        this.clothingProduct.getId() + 1);
    }

    /**
     * Deletes all clothing items from database.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @AfterEach
    void clearDatabase() {
        clothingItemRepository.deleteAll();
        clothingProductRepository.deleteAll();
    }

    /**
     * Test retrieving a nonexistent clothing item.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void getNonExistentClothingItem() {
        // Arrange
        // use any other item id than the one currently in db
        int id = 200;
        when(clothingItemRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThrows(
                FashionStoreException.class,
                () -> clothingItemService.getClothingItem(clothingProduct.getId(), id),
                "Trying to find non existent clothing item ID should not find anything.");
    }

    /**
     * Helper assert function.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Disabled("Helper function")
    void assertClothingItem(ClothingItem expected, ClothingItem actual) {
        assertEquals(
                actual.getColour(),
                expected.getColour(),
                "Response ClothingItem's colour does not match.");
        assertEquals(
                actual.getClothingProduct(),
                expected.getClothingProduct(),
                "Response ClothingItem's ClothingProduct does not match.");
        assertEquals(
                actual.getId(), expected.getId(), "Response ClothingItem's id does not match.");
    }

    /**
     * Test retrieving an existent clothing item, and check that details are correct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void getExistingClothingItem() {
        // Arrange
        when(clothingProductRepository.findById(clothingProduct.getId()))
                .thenReturn(Optional.of(clothingProduct));
        when(clothingItemRepository.findById(clothingItem.getId()))
                .thenReturn(Optional.of(clothingItem));
        // Act
        // Assert
        ClothingItem response =
                assertDoesNotThrow(
                        () ->
                                clothingItemService.getClothingItem(
                                        clothingProduct.getId(), clothingItem.getId()),
                        "Trying to get a valid, existing clothing item throws an exception.");

        assertClothingItem(clothingItem, response);

        verify(clothingProductRepository, times(1)).findById(clothingProduct.getId());
        verify(clothingItemRepository, times(1)).findById(clothingItem.getId());
    }

    /**
     * Test creating a clothing item.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void createClothingItem() {
        assertDoesNotThrow(
                () ->
                        clothingItemService.createClothingItem(
                                this.validClothingItemRequestDto, this.clothingProduct.getId()),
                "Creating a clothing item with valid details should not throw an exception.");
    }

    /**
     * Test creating a clothing item with invalid product ID.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void createClothingItemInvalidId1() {
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () ->
                                clothingItemService.createClothingItem(
                                        this.invalidClothingItemRequestDto,
                                        this.clothingProduct.getId() + 1),
                        "Creating a clothing item with an invalid product ID should throw an exception.");

        assertEquals(
                HttpStatus.NOT_FOUND,
                e.getStatus(),
                "Invalid product ID should throw a NOT_FOUND execption.");
        assertEquals(
                String.format(
                        "ClothingProduct ID %d was not found.",
                        this.invalidClothingItemRequestDto.clothingProductId()),
                e.getMessage(),
                "Exception message should indicate that the clothing product was not found.");
    }

    /**
     * Test creating a clothing item with different product IDs.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void createClothingItemInvalidId2() {
        int invalidId = clothingItem.getId() + 1;
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () ->
                                clothingItemService.createClothingItem(
                                        this.validClothingItemRequestDto, invalidId),
                        "Creating a clothing item with different product IDs should throw an exception.");

        assertEquals(
                HttpStatus.BAD_REQUEST,
                e.getStatus(),
                "Different product IDs should throw a BAD_REQUEST exception.");
        assertEquals(
                String.format(
                        "Path variable productId %d does not match clothingProductId in request body %d.",
                        invalidId, this.validClothingItemRequestDto.clothingProductId()),
                e.getMessage(),
                "Exception message should indicate that the clothing product IDs do not match.");
    }
}
