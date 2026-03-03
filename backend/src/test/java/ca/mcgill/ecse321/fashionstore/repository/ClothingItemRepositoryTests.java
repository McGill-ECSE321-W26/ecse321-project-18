package ca.mcgill.ecse321.fashionstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test suite for clothing item persistence in the database.
 *
 * @author Flavie Qin (flavieq88)
 */
@SpringBootTest
class ClothingItemRepositoryTests {
    @Autowired private ClothingItemRepository clothingItemRepository;
    private ClothingItem clothingItem;

    @Autowired private ClothingProductRepository clothingProductRepository;
    private ClothingProduct clothingProduct;

    @BeforeEach
    void createClothingItem() {
        // Create clothingProduct to associate with
        String name = "Crewneck";
        float price = 59.99f;
        ClothingProduct newClothingProduct = new ClothingProduct();
        newClothingProduct.setName(name);
        newClothingProduct.setPrice(price);
        clothingProductRepository.save(newClothingProduct);

        // create clothing item
        ClothingItem newClothingItem = new ClothingItem();
        newClothingItem.setClothingProduct(newClothingProduct);
        newClothingItem.setSize(ClothingItem.Size.S);
        newClothingItem.setColour(ClothingItem.Colour.PINK);
        newClothingItem.setNumInStock(10);

        // Save the clothing product and item
        clothingProductRepository.save(newClothingProduct);
        clothingProduct = newClothingProduct;
        clothingItemRepository.save(newClothingItem);
        clothingItem = newClothingItem;
    }

    /**
     * Clears database after each test.
     *
     * @author Flavie Qin (flavieq88)
     */
    @AfterEach
    void clearDatabase() {
        clothingItemRepository.deleteAll();
        clothingProductRepository.deleteAll();
    }

    /**
     * Test retrieval of clothing item from database is correct.
     *
     * @author Flavie Qin (flavieq88)
     */
    @Test
    void testPersistAndLoadClothingItem() {
        // Read clothing item from database
        ClothingItem clothingItemFromDb =
                clothingItemRepository.findClothingItemById(clothingItem.getId());

        // Assert clothing item found
        assertNotNull(clothingItemFromDb, "Could not find saved clothing item in database.");
    }

    /**
     * Test information of clothing item from database is persisted correctly.
     *
     * @author Flavie Qin (flavieq88)
     */
    @Test
    void testReadClothingItemInfo() {
        // Read clothing item from database
        ClothingItem clothingItemFromDb =
                clothingItemRepository.findClothingItemById(clothingItem.getId());

        assertEquals(
                clothingItem.getId(),
                clothingItemFromDb.getId(),
                "Clothing item id was incorrectly saved in the database.");
        assertEquals(
                clothingItem.getNumInStock(),
                clothingItemFromDb.getNumInStock(),
                "Clothing item numInStock was incorrectly saved in the database.");
        assertEquals(
                clothingItem.getColour(),
                clothingItemFromDb.getColour(),
                "Clothing item colour was incorrectly saved in the database.");
        assertEquals(
                clothingItem.getSize(),
                clothingItemFromDb.getSize(),
                "Clothing item size was incorrectly saved in the database");
    }

    /**
     * Test associated clothing product of clothing item from database is persisted correctly.
     *
     * @author Flavie Qin (flavieq88)
     */
    @Test
    void testReadClothingItemAssociation() {
        // Read clothing item from database
        ClothingItem clothingItemFromDb =
                clothingItemRepository.findClothingItemById(clothingItem.getId());

        assertEquals(
                clothingProduct.getId(),
                clothingItemFromDb.getClothingProduct().getId(),
                "Clothing item's clothing product was incorrectly saved in the database");
    }

    /**
     * Test deletion of clothing item from database works.
     *
     * @author Flavie Qin (flavieq88)
     */
    @Test
    void testDeleteClothingItem() {
        // delete item in repository
        clothingItemRepository.delete(clothingItem);

        // Try to read clothing item from database
        ClothingItem clothingItemFromDb =
                clothingItemRepository.findClothingItemById(clothingItem.getId());

        // Assert clothing item not found
        assertNull(clothingItemFromDb, "Clothing item was not successfully deleted in database.");

        // Clothing product should not have been deleted
        ClothingProduct clothingProductFromDb =
                clothingProductRepository.findClothingProductById(clothingProduct.getId());
        assertNotNull(clothingProductFromDb, "Clothing product incorrectly deleted from database.");
    }

    /**
     * Test deletion of clothing item by id from database works.
     *
     * @author Flavie Qin (flavieq88)
     */
    @Test
    @Transactional
    void testDeleteClothingItemById() {
        // delete item in repository
        clothingItemRepository.deleteClothingItemById(clothingItem.getId());

        // Try to read clothing item from database
        ClothingItem clothingItemFromDb =
                clothingItemRepository.findClothingItemById(clothingItem.getId());

        // Assert clothing item not found
        assertNull(clothingItemFromDb, "Clothing item was not successfully deleted in database.");

        // Clothing product should not have been deleted
        ClothingProduct clothingProductFromDb =
                clothingProductRepository.findClothingProductById(clothingProduct.getId());
        assertNotNull(clothingProductFromDb, "Clothing product incorrectly deleted from database.");
    }

    /**
     * Test deletion of clothing item by numInStock from database works.
     *
     * @author Flavie Qin (flavieq88)
     */
    @Test
    @Transactional
    void testDeleteClothingItemByNumInStock() {
        // delete item in repository
        clothingItemRepository.deleteByNumInStock(clothingItem.getNumInStock());

        // Try to read clothing item from database
        ClothingItem clothingItemFromDb =
                clothingItemRepository.findClothingItemById(clothingItem.getId());

        // Assert clothing item not found
        assertNull(clothingItemFromDb, "Clothing item was not successfully deleted in database.");

        // Clothing product should not have been deleted
        ClothingProduct clothingProductFromDb =
                clothingProductRepository.findClothingProductById(clothingProduct.getId());
        assertNotNull(clothingProductFromDb, "Clothing product incorrectly deleted from database.");
    }

    /**
     * Test updating numOfStock of clothing item from database is saved correctly.
     *
     * @author Flavie Qin (flavieq88)
     */
    @Test
    void testUpdateClothingItemNumInStock() {
        int newNumInStock = 100;
        clothingItem.setNumInStock(newNumInStock);

        // Save updated clothing item from database
        clothingItemRepository.save(clothingItem);

        // Read updated clothing item from database
        ClothingItem clothingItemFromDb =
                clothingItemRepository.findClothingItemById(clothingItem.getId());

        // Assert correct responses
        assertEquals(
                newNumInStock,
                clothingItemFromDb.getNumInStock(),
                "Clothing item numInStock was incorrectly saved in the database.");
    }

    /**
     * Test updating colour of clothing item from database is saved correctly.
     *
     * @author Flavie Qin (flavieq88)
     */
    @Test
    void testUpdateClothingItemColour() {
        ClothingItem.Colour newColour = ClothingItem.Colour.BLACK;
        clothingItem.setColour(newColour);

        // Save updated clothing item from database
        clothingItemRepository.save(clothingItem);

        // Read updated clothing item from database
        ClothingItem clothingItemFromDb =
                clothingItemRepository.findClothingItemById(clothingItem.getId());

        // Assert correct responses
        assertEquals(
                newColour,
                clothingItemFromDb.getColour(),
                "Clothing item colour was incorrectly saved in the database.");
    }

    /**
     * Test updating size of clothing item from database is saved correctly.
     *
     * @author Flavie Qin (flavieq88)
     */
    @Test
    void testUpdateClothingItemSize() {
        ClothingItem.Size newSize = ClothingItem.Size.XL;
        clothingItem.setSize(newSize);

        // Save updated clothing item from database
        clothingItemRepository.save(clothingItem);

        // Read updated clothing item from database
        ClothingItem clothingItemFromDb =
                clothingItemRepository.findClothingItemById(clothingItem.getId());

        // Assert correct responses
        assertEquals(
                newSize,
                clothingItemFromDb.getSize(),
                "Clothing item size was incorrectly saved in the database.");
    }
}
