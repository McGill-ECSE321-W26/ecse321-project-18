package ca.mcgill.ecse321.fashionstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Order;
import java.sql.Date;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tests for customer persistence.
 *
 * @author Jennifer You (jenni4u)
 */
@SpringBootTest
class CustomerRepositoryTests {

    @Autowired private CustomerRepository customerRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ClothingItemRepository clothingItemRepository;
    @Autowired private ClothingProductRepository clothingProductRepository;
    private Customer customer;
    private Order order;
    private ClothingItem item;

    /**
     * Create a customer in the database before each test.
     *
     * @author Jennifer You (jenni4u)
     */
    @BeforeEach
    public void createCustomer() {
        Customer newCustomer = new Customer();
        newCustomer.setEmail("mimi@kittycat.com");
        newCustomer.setPassword("meowmeow");
        newCustomer.setAddress("123 Cat Street");
        newCustomer.setNumLoyaltyPoints(100);
        customerRepository.save(newCustomer);

        // save customer reference for tests
        customer = newCustomer;
    }

    /**
     * Clear up databases after each test.
     *
     * @author Jennifer You (jenni4u)
     */
    @AfterEach
    public void clearDatabase() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        clothingItemRepository.deleteAll();
        clothingProductRepository.deleteAll();
    }

    /**
     * Test read and write of the Customer object in the persistence layer.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void testCustomerLoad() {
        assertNotNull(
                customerRepository.findCustomerByEmail(customer.getEmail()),
                "Persistence did not save the customer");
    }

    /**
     * Test deleting Customer object in the persistence layer.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    @Transactional
    void testCustomerDelete() {
        customerRepository.deleteCustomerByEmail(customer.getEmail());
        assertNull(
                customerRepository.findCustomerByEmail("mimi@kittycat.com"),
                "Persistence did not delete the customer");
    }

    /**
     * Test read the Customer object's attributes in the persistence layer.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void testCustomerReadAttributes() {
        Customer customerFromDb = customerRepository.findCustomerByEmail(customer.getEmail());
        assertEquals(
                customer.getPassword(),
                customerFromDb.getPassword(),
                "Persistence did not return the same password attribute");
        assertEquals(
                customer.getAddress(),
                customerFromDb.getAddress(),
                "Persistence did not return the same address attribute");
        assertEquals(
                customer.getNumLoyaltyPoints(),
                customerFromDb.getNumLoyaltyPoints(),
                "Persistence did not return the same numLoyaltyPoints attribute");
    }

    /**
     * Test changing the Customer object's attributes in the persistence layer.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void testCustomerWriteAttributes() {
        customer.setPassword("woofie");
        customer.setAddress("456 Dog Street");
        customer.setNumLoyaltyPoints(200);
        customerRepository.save(customer);
        Customer customerFromDb = customerRepository.findCustomerByEmail(customer.getEmail());
        // attributes
        assertEquals(
                customer.getPassword(),
                customerFromDb.getPassword(),
                "Persistence did not update the password attribute");
        assertEquals(
                customer.getAddress(),
                customerFromDb.getAddress(),
                "Persistence did not update the address attribute");
        assertEquals(
                customer.getNumLoyaltyPoints(),
                customerFromDb.getNumLoyaltyPoints(),
                "Persistence did not update the numLoyaltyPoints attribute");
    }

    /**
     * Test initialization of the Customer object's references in the persistence layer.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    @Transactional
    void testCustomerInitializeReferences() {
        Customer customerFromDb = customerRepository.findCustomerByEmail(customer.getEmail());
        assertFalse(
                customerFromDb.hasShoppingCart(),
                "Persistence did not initialize the shopping cart reference");
        assertFalse(
                customerFromDb.hasPurchasedOrders(),
                "Persistence did not initialize the purchased orders reference");
    }

    /**
     * Helper method to initialize reference objects for testing Customer references in the
     * persistence layer.
     *
     * @author Jennifer You (jenni4u)
     */
    private void initializeReferenceObjects() {
        // create clothing item
        item = new ClothingItem();
        item.setNumInStock(10);
        item.setColour(ClothingItem.Colour.PINK);
        item.setSize(ClothingItem.Size.M);
        clothingItemRepository.save(item);

        // create order
        order = new Order();
        order.setState(Order.State.ASSIGNED);
        order.setOrderDate(Date.valueOf(LocalDate.now()));
        order.setDeliveryDate(Date.valueOf(LocalDate.now()));
        order.setDeliveryAddress("123 Cat Street");
        order.setPrice(50.0f);
        order.setCustomer(customerRepository.findCustomerByEmail(customer.getEmail()));
        orderRepository.save(order);
    }

    /**
     * Test adding Customer references to orders and items in the persistence layer.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    @Transactional
    void testCustomerWriteReferences() {
        initializeReferenceObjects();
        // write references
        customer.addShoppingCart(item);
        customer.addPurchasedOrder(order);
        customerRepository.save(customer);
        Customer customerFromDb = customerRepository.findCustomerByEmail(customer.getEmail());
        ClothingItem itemFromDb = clothingItemRepository.findClothingItemById(item.getId());
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertEquals(
                itemFromDb,
                customerFromDb.getShoppingCart(customer.indexOfShoppingCart(item)),
                "Persistence did not update the shopping cart reference");
        assertEquals(
                orderFromDb,
                customerFromDb.getPurchasedOrder(customer.indexOfPurchasedOrder(order)),
                "Persistence did not update the purchased orders reference");
    }

    /**
     * Test deleting Customer references to orders and items in the persistence layer.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    @Transactional
    void testCustomerDeleteReferences() {
        initializeReferenceObjects();
        customer.removePurchasedOrder(order);
        customer.removeShoppingCart(item);
        customerRepository.save(customer);
        assertEquals(
                -1,
                customer.indexOfShoppingCart(item),
                "Persistence did not remove the shopping cart reference");
        assertEquals(
                -1,
                customer.indexOfPurchasedOrder(order),
                "Persistence did not remove the purchased orders reference");
    }
}
