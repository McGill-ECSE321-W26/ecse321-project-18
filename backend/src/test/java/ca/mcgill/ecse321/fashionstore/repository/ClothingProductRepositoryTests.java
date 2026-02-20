package ca.mcgill.ecse321.fashionstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * {@summary Test suite for clothing product persistence in the database.}
 *
 * @author Qiuyu Huang (redacted24)
 */
@SpringBootTest
class ClothingProductRepositoryTests {
    @Autowired private ClothingProductRepository clothingProductRepository;
    private ClothingProduct clothingProduct;

    @BeforeEach
    void createClothingProduct() {
        // Create clothingProduct
        String name = "Crewneck";
        float price = 59.99f;
        String image = "someimagepath.jpg";
        ClothingProduct newClothingProduct = new ClothingProduct();
        newClothingProduct.setName(name);
        newClothingProduct.setPrice(price);
        newClothingProduct.setImage(image);

        // Save the clothing product
        clothingProductRepository.save(newClothingProduct);
        clothingProduct = newClothingProduct;
    }

    /**
     * Clears database after each test.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @AfterEach
    void clearDatabase() {
        clothingProductRepository.deleteAll();
    }

    /**
     * Test retrieval of clothing product from database is not null.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void testPersistAndLoadClothingProduct() {
        // Read person from database
        ClothingProduct clothingProductFromDb =
                clothingProductRepository.findClothingProductById(clothingProduct.getId());

        // Assert correct response
        assertNotNull(clothingProductFromDb, "Could not find saved clothing product in database.");
    }

    /**
     * Test retrieval of clothing product name is correct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void testPersistAndLoadClothingProductName() {
        // Read person from database
        ClothingProduct clothingProductFromDb =
                clothingProductRepository.findClothingProductById(clothingProduct.getId());
        assertEquals(
                clothingProduct.getName(),
                clothingProductFromDb.getName(),
                "Clothing product name is not saved in database.");
    }

    /**
     * Test retrieval of clothing product price is correct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void testPersistAndLoadClothingProductPrice() {
        // Read person from database
        ClothingProduct clothingProductFromDb =
                clothingProductRepository.findClothingProductById(clothingProduct.getId());
        assertEquals(
                clothingProduct.getPrice(),
                clothingProductFromDb.getPrice(),
                "Clothing product price is not saved in database.");
    }

    /**
     * Test retrieval of clothing product image string is correct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void testPersistAndLoadClothingProductImageString() {
        // Read person from database
        ClothingProduct clothingProductFromDb =
                clothingProductRepository.findClothingProductById(clothingProduct.getId());
        assertEquals(
                clothingProduct.getImage(),
                clothingProductFromDb.getImage(),
                "Clothing product image is not saved in database.");
    }
}
