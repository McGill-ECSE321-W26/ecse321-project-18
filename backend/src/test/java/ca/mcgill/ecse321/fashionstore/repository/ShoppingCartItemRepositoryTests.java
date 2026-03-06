package ca.mcgill.ecse321.fashionstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.ShoppingCartItem;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test suite for shopping cart item persistence in the database.
 *
 * @author Cyrus Fung (cfung89)
 */
@SpringBootTest
class ShoppingCartItemRepositoryTests {
    @Autowired private CustomerRepository customerRepositoryRepository;
    private Customer customerRepository;

    @Autowired private ShoppingCartItemRepository shoppingCartItemRepository;
    private ShoppingCartItem shoppingCartItem;

    @Autowired private ClothingItemRepository clothingItemRepository;
    private ClothingItem clothingItem;

    /**
     * Setup method for ShoppingCartItem.
     *
     * @author Cyrus Fung (cfung89)
     */
    @BeforeEach
    void setup() {
        customerRepository = createCustomer();
        shoppingCartItem = createShoppingCartItem();
        clothingItem = createClothingItem();

        shoppingCartItem.setClothingItem(clothingItem);
        shoppingCartItem.setCustomer(customerRepository);
        shoppingCartItemRepository.save(shoppingCartItem);
    }

    /**
     * Create a ClothingItem before each test.
     *
     * @author Cyrus Fung (cfung89)
     */
    private ClothingItem createClothingItem() {
        ClothingItem newClothingItem = new ClothingItem();
        newClothingItem.setSize(ClothingItem.Size.S);
        newClothingItem.setColour(ClothingItem.Colour.BLUE);
        newClothingItem.setNumInStock(10);

        return clothingItemRepository.save(newClothingItem);
    }

    /**
     * Create a Customer before each test.
     *
     * @author Cyrus Fung (cfung89)
     */
    private Customer createCustomer() {
        Customer newCustomer = new Customer();
        newCustomer.setNumLoyaltyPoints(49);
        newCustomer.setAddress("123 Lim");
        newCustomer.setEmail("hello@fashionstore.com");
        newCustomer.setPassword("thisismypassword!");

        return customerRepositoryRepository.save(newCustomer);
    }

    /**
     * Create a ShoppingCartItem before each test.
     *
     * @author Cyrus Fung (cfung89)
     */
    private ShoppingCartItem createShoppingCartItem() {
        ShoppingCartItem newShoppingCartItem = new ShoppingCartItem();
        newShoppingCartItem.setQuantity(2);

        return shoppingCartItemRepository.save(newShoppingCartItem);
    }

    /**
     * Clears database after each test.
     *
     * @author Cyrus Fung (cfung89)
     */
    @AfterEach
    void clearDatabase() {
        shoppingCartItemRepository.deleteAll();
        customerRepositoryRepository.deleteAll();
        clothingItemRepository.deleteAll();
    }

    /**
     * Test retrieval of shopping cart item from database.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testPersisteAndLoadShoppingCartItem() {
        ShoppingCartItem shoppingCartItemFromDb =
                shoppingCartItemRepository.findShoppingCartItemById(shoppingCartItem.getId());
        assertNotNull(
                shoppingCartItemFromDb, "Could not find saved shopping cart item in database.");
    }

    /**
     * Test information of shopping cart item from database.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testReadShoppingCartItemInfo() {
        // Read shopping cart item from database
        ShoppingCartItem shoppingCartItemFromDb =
                shoppingCartItemRepository.findShoppingCartItemById(shoppingCartItem.getId());

        assertEquals(
                shoppingCartItem.getId(),
                shoppingCartItemFromDb.getId(),
                "Shopping cart item id was incorrectly saved in the database.");
        assertEquals(
                shoppingCartItem.getQuantity(),
                shoppingCartItemFromDb.getQuantity(),
                "Shopping cart item quantity was incorrectly saved in the database.");
    }

    /**
     * Test associated customerRepository of shopping cart item from database is persisted
     * correctly.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Transactional
    void testReadCustomerAssociation() {
        // Read clothing item from database
        ShoppingCartItem shoppingCartItemFromDb =
                shoppingCartItemRepository.findShoppingCartItemById(shoppingCartItem.getId());

        assertEquals(
                customerRepository.getId(),
                shoppingCartItemFromDb.getCustomer().getId(),
                "ShoppingCartItem's Customer was incorrectly saved in the database");
        assertEquals(
                customerRepository.getNumLoyaltyPoints(),
                shoppingCartItemFromDb.getCustomer().getNumLoyaltyPoints(),
                "Customer's numLoyaltyPoints from ShoppingCartItem was incorrectly saved in the database");
        assertEquals(
                customerRepository.getAddress(),
                shoppingCartItemFromDb.getCustomer().getAddress(),
                "Customer's address from ShoppingCartItem was incorrectly saved in the database");
        assertEquals(
                1,
                shoppingCartItemFromDb.getCustomer().getShoppingCartItems().size(),
                "Customer's length of items from ShoppingCartItem was incorrectly saved in the database");
    }

    /**
     * Test associated clothing item of shopping cart item from database is persisted correctly.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Transactional
    void testReadClothingItemAssociation() {
        // Read clothing item from database
        ShoppingCartItem shoppingCartItemFromDb =
                shoppingCartItemRepository.findShoppingCartItemById(shoppingCartItem.getId());

        assertEquals(
                clothingItem.getId(),
                shoppingCartItemFromDb.getClothingItem().getId(),
                "Shopping cart item's clothingItem was incorrectly saved in the database");
        assertEquals(
                clothingItem.getSize(),
                shoppingCartItemFromDb.getClothingItem().getSize(),
                "ClothingItem's size from ShoppingCartItem was incorrectly saved in the database");
        assertEquals(
                clothingItem.getColour(),
                shoppingCartItemFromDb.getClothingItem().getColour(),
                "ClothingItem's colour from ShoppingCartItem was incorrectly saved in the database");
        assertEquals(
                clothingItem.getNumInStock(),
                shoppingCartItemFromDb.getClothingItem().getNumInStock(),
                "ClothingItem's numInStock from ShoppingCartItem was incorrectly saved in the database");
    }

    /**
     * Test deletion of customerRepository from database works.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testDeleteShoppingCartItem() {
        // delete item in repository
        shoppingCartItemRepository.delete(shoppingCartItem);

        // Try to read clothing item from database
        ShoppingCartItem shoppingCartItemFromDb =
                shoppingCartItemRepository.findShoppingCartItemById(shoppingCartItem.getId());

        // Assert clothing item not found
        assertNull(
                shoppingCartItemFromDb,
                "Shopping cart item was not successfully deleted in database.");

        // Customer should not have been deleted
        Customer customerRepositoryFromDb =
                customerRepositoryRepository.findCustomerById(customerRepository.getId());
        assertNotNull(customerRepositoryFromDb, "Customer incorrectly deleted from database.");

        // ClothingItem should not have been deleted
        ClothingItem clothingItemFromDb =
                clothingItemRepository.findClothingItemById(clothingItem.getId());
        assertNotNull(clothingItemFromDb, "ClothingItem incorrectly deleted from database.");
    }

    /**
     * Test updating numOfStock of shopping cart item from database is saved correctly.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testUpdateShoppingCartItemQuantity() {
        int newQuantity = 100;
        shoppingCartItem.setQuantity(newQuantity);

        // Save updated shopping cart item from database
        shoppingCartItemRepository.save(shoppingCartItem);

        // Read updated shopping cart item from database
        ShoppingCartItem shoppingCartItemFromDb =
                shoppingCartItemRepository.findShoppingCartItemById(shoppingCartItem.getId());

        // Assert correct responses
        assertEquals(
                newQuantity,
                shoppingCartItemFromDb.getQuantity(),
                "Shopping cart item quantity was incorrectly saved in the database.");
    }
}
