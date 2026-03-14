package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.mcgill.ecse321.fashionstore.dto.ClothingItemRequestDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

/**
 * Test suite for clothing item service class.
 *
 * @author Qiuyu Huang (redacted24)
 */
@SpringBootTest
class ClothingItemServiceTests {
    @Autowired private ClothingItemService clothingItemService;
    @Autowired private ClothingProductRepository clothingProductRepository;
    @Autowired private ClothingItemRepository clothingItemRepository;

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
        clothingProductRepository.save(clothingProduct);
        this.clothingProduct = clothingProduct;

        // create clothing item
        ClothingItem clothingItem = new ClothingItem();
        clothingItem.setClothingProduct(clothingProduct);
        clothingItem.setSize(ClothingItem.Size.M);
        clothingItem.setColour(ClothingItem.Colour.YELLOW);
        clothingItem.setNumInStock(100);
        clothingItemRepository.save(clothingItem);
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
        // use any other item id than the one currently in db
        int id = clothingItem.getId() + 1;

        assertThrows(
                FashionStoreException.class,
                () -> clothingItemService.getClothingItem(clothingProduct.getId(), id),
                "Trying to find non existent clothing item ID should not find anything.");
    }

    /**
     * Test retrieving an existent clothing item, and check that details are correct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void getExistingClothingItem() {
        assertDoesNotThrow(
                () ->
                        clothingItemService.getClothingItem(
                                clothingProduct.getId(), clothingItem.getId()),
                "Trying to get an existing clothing item returns it.");
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
