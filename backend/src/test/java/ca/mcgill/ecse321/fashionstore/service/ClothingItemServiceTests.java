package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
     * Test updating stock of an existing clothing item.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void updateClothingItemStock_success() {
        ClothingItemRequestDto dto =
                new ClothingItemRequestDto(
                        clothingItem.getSize(),
                        clothingItem.getColour(),
                        50,
                        clothingProduct.getId());

        ClothingItem updated =
                clothingItemService.updateClothingItemStock(
                        clothingProduct.getId(), clothingItem.getId(), dto);

        assertEquals(50, updated.getNumInStock(), "Stock should update to 50.");
    }

    /**
     * Test updating stock of a non-existent clothing item.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void updateClothingItemStock_nonExistentItem() {
        ClothingItemRequestDto dto =
                new ClothingItemRequestDto(
                        clothingItem.getSize(),
                        clothingItem.getColour(),
                        50,
                        clothingProduct.getId());

        int badId = clothingItem.getId() + 999;

        assertThrows(
                FashionStoreException.class,
                () ->
                        clothingItemService.updateClothingItemStock(
                                clothingProduct.getId(), badId, dto),
                "Updating a non-existent clothing item should throw.");
    }

    /**
     * Test updating stock under the wrong product.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void updateClothingItemStock_wrongProduct() {
        ClothingProduct otherProduct = new ClothingProduct();
        otherProduct.setImage("shirt.png");
        otherProduct.setPrice(29.99f);
        otherProduct.setName("Shirts");
        clothingProductRepository.save(otherProduct);

        ClothingItemRequestDto dto =
                new ClothingItemRequestDto(
                        clothingItem.getSize(),
                        clothingItem.getColour(),
                        50,
                        clothingProduct.getId());

        assertThrows(
                FashionStoreException.class,
                () ->
                        clothingItemService.updateClothingItemStock(
                                otherProduct.getId(), clothingItem.getId(), dto),
                "Updating an item under the wrong product should throw.");
    }

    /**
     * Test deleting an existing clothing item.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void deleteClothingItem_success() {
        assertDoesNotThrow(
                () ->
                        clothingItemService.deleteClothingItem(
                                clothingProduct.getId(), clothingItem.getId()),
                "Deleting an existing clothing item should not throw.");

        assertFalse(
                clothingItemRepository.existsById(clothingItem.getId()),
                "Clothing item should be deleted.");
    }

    /**
     * Test deleting a clothing item under the wrong product.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void deleteClothingItem_wrongProduct() {
        ClothingProduct otherProduct = new ClothingProduct();
        otherProduct.setImage("pants.png");
        otherProduct.setPrice(49.99f);
        otherProduct.setName("Pants");
        clothingProductRepository.save(otherProduct);

        assertThrows(
                FashionStoreException.class,
                () ->
                        clothingItemService.deleteClothingItem(
                                otherProduct.getId(), clothingItem.getId()),
                "Deleting under the wrong product should throw.");
    }

    /**
     * Test deleting a non-existent clothing item.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void deleteClothingItem_nonExistent() {
        int badId = clothingItem.getId() + 28;

        assertThrows(
                FashionStoreException.class,
                () -> clothingItemService.deleteClothingItem(clothingProduct.getId(), badId),
                "Deleting a non-existent clothing item should throw.");
    }
}
