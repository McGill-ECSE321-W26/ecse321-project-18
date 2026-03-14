package ca.mcgill.ecse321.fashionstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test suite for clothing product persistence in the database.
 *
 * @author Qiuyu Huang (redacted24)
 */
@SpringBootTest
@Transactional
class ClothingProductRepositoryTests {
    @Autowired private ClothingProductRepository clothingProductRepository;
    private ClothingProduct clothingProduct;
    @Autowired private ClothingItemRepository clothingItemRepository;

    /**
     * Setup method to run before each test. Builds a mock clothingProduct.
     *
     * @author Qiuyu Huang (redacted24)
     */
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
        ClothingProduct clothingProductFromDb =
                clothingProductRepository.findClothingProductById(clothingProduct.getId());

        // Assert correct response
        assertNotNull(clothingProductFromDb, "Could not find saved clothing product in database.");
    }

    /**
     * Test retrieval of clothing product name from database is correct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void testPersistAndLoadClothingProductName() {
        ClothingProduct clothingProductFromDb =
                clothingProductRepository.findClothingProductById(clothingProduct.getId());
        assertEquals(
                clothingProduct.getName(),
                clothingProductFromDb.getName(),
                "Clothing product name is not saved in database.");
    }

    /**
     * Test retrieval of clothing product price from database is correct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void testPersistAndLoadClothingProductPrice() {
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
        ClothingProduct clothingProductFromDb =
                clothingProductRepository.findClothingProductById(clothingProduct.getId());
        assertEquals(
                clothingProduct.getImage(),
                clothingProductFromDb.getImage(),
                "Clothing product image is not saved in database.");
    }

    /**
     * Test finding clothing products by name using a case-insensitive contains.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void findClothingProductsByNameCaseInsensitive() {
        // create clothing products and prepare to search
        String search = "cReW";
        ClothingProduct crewneck = createSaveClothingProduct("CRewneck", 50.0f, "imagepath.jpg");
        createSaveClothingProduct("T-shirt", 20.25f, "imagepath2.jpg");
        List<ClothingProduct> expected = List.of(clothingProduct, crewneck);

        List<ClothingProduct> actual =
                clothingProductRepository.findClothingProductsByNameContainsIgnoreCase(search);

        assertIterableEquals(
                expected, actual, "Clothing product search by name did not find correct results.");
    }

    /**
     * Test updating clothing product name reflects in database.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void updateClothingProductName() {
        String newName = "Hoodie";
        clothingProduct.setName(newName);
        ClothingProduct clothingProductFromDb =
                clothingProductRepository.findClothingProductById(clothingProduct.getId());
        assertEquals(
                newName,
                clothingProductFromDb.getName(),
                "Clothing product name update is not updated in database.");
    }

    /**
     * Test that updating clothing product price reflects in database.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void updateClotihngProductPrice() {
        float newPrice = 24.99f;
        clothingProduct.setPrice(newPrice);
        ClothingProduct clothingProductFromDb =
                clothingProductRepository.findClothingProductById(clothingProduct.getId());
        assertEquals(
                newPrice,
                clothingProductFromDb.getPrice(),
                "Clothing product price update is not reflected in database.");
    }

    /**
     * Test that updating clothing product image string reflects in database.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void updateClothingProductImageString() {
        String newImageString = "../../here.jpg";
        clothingProduct.setImage(newImageString);
        ClothingProduct clothingProductFromDb =
                clothingProductRepository.findClothingProductById(clothingProduct.getId());
        assertEquals(
                newImageString,
                clothingProductFromDb.getImage(),
                "Clothing product image string update is not reflected in database.");
    }

    /**
     * Test that deleting a clothing product removes it from database.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void deleteClothingProduct() {
        clothingProductRepository.deleteClothingProductById(clothingProduct.getId());
        ClothingProduct clothingProductFromDb =
                clothingProductRepository.findClothingProductById(clothingProduct.getId());
        assertNull(
                clothingProductFromDb,
                "Clothing product still exists in database after its deletion.");
    }

    /**
     * Test that deleting a clothing product deletes all clothing items related to it.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void deleteClothingProductComposition() {
        // create clothing item and set its values
        ClothingItem clothingItem = new ClothingItem();
        clothingItem.setClothingProduct(clothingProduct);
        clothingItem.setColour(ClothingItem.Colour.BLACK);
        clothingItem.setSize(ClothingItem.Size.L);
        clothingItem.setNumInStock(200);
        clothingItemRepository.save(clothingItem);

        // delete clothing product
        clothingProductRepository.deleteClothingProductById(clothingProduct.getId());

        // check that clothing item isn't in database.
        ClothingItem clothingItemFromDb =
                clothingItemRepository.findClothingItemById(clothingItem.getId());
        assertNull(
                clothingItemFromDb,
                "Clothing item still exists even if associated clothing product is deleted.");
    }

    /**
     * Helper method to create and save a clothing product with the specified details.
     *
     * @param name Name of the new ClothingProduct
     * @param price Price of the new ClothingProduct
     * @param image Image path of the new ClothingProduct
     * @return the newly created and saved ClothingProduct
     * @author Carolyn Wu (cw118)
     */
    private ClothingProduct createSaveClothingProduct(String name, float price, String image) {
        ClothingProduct product = new ClothingProduct();
        product.setName(name);
        product.setPrice(price);
        product.setImage(image);

        // save to database
        clothingProductRepository.save(product);

        return product;
    }
}
