package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.springframework.transaction.annotation.Transactional;

/** Test suite for clothing product service class. */
@SpringBootTest
class ClothingProductServiceTests {
    @Autowired private ClothingProductService clothingProductService;
    @Autowired private ClothingProductRepository clothingProductRepository;
    @Autowired private ClothingItemRepository clothingItemRepository;

    private ClothingProduct clothingProduct;
    private ClothingItem clothingItem;

    /**
     * Creates and saves a clothing product, and a clothing item.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @BeforeEach
    void createClothingItems() {
        // set clothing product first
        ClothingProduct clothingProduct = new ClothingProduct();
        clothingProduct.setImage("hoodie.png");
        clothingProduct.setPrice(69.99f);
        clothingProduct.setName("Hoodie");
        clothingProductRepository.save(clothingProduct);
        this.clothingProduct = clothingProduct;

        // create first clothing item
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
     * Test retrieving a non-existing clothingProduct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void getNonExistingClothingProduct() {
        int id = clothingProduct.getId() + 1;
        assertThrows(
                FashionStoreException.class, () -> clothingProductService.getClothingProduct(id));
    }

    /**
     * Test retrieving an existing clothingProduct. Does not check for clothing items.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void getClothingProductOnly() {
        int id = clothingProduct.getId();
        assertDoesNotThrow(() -> clothingProductService.getClothingProduct(id));
        ClothingProduct response = clothingProductService.getClothingProduct(id);

        assertEquals(
                clothingProduct.getImage(),
                response.getImage(),
                "Image path of retrieved ClothingProduct does not match.");
        assertEquals(
                clothingProduct.getName(),
                response.getName(),
                "Name of retrieved ClothingProduct does not match.");
        assertEquals(
                clothingProduct.getPrice(),
                response.getPrice(),
                "Price of retrieved ClothingProduct does not match.");
    }

    /**
     * Test retrieving an existing clothingProduct and check for its clothing items. It is assumed
     * that the GET works and returns a valid ClothingProduct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    @Transactional
    void getClothingProductCheckItems() {
        ClothingProduct response =
                clothingProductService.getClothingProduct(clothingProduct.getId());
        assertNotNull(response.getItems(), "ClothingProduct items list should not be null.");
        assertEquals(
                1,
                response.getItems().size(),
                String.format(
                        "ClothingProduct item list size is %d, should be %d.",
                        response.getItems().size(), clothingProduct.getItems().size()));
        assertEquals(
                clothingItem.getColour(),
                response.getItem(0).getColour(),
                "Retrieved clothing product's item does not have correct colour.");
    }
}
