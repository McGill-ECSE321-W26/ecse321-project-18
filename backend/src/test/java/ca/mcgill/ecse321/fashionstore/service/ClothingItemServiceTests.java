package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
