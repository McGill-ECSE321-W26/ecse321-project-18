package ca.mcgill.ecse321.fashionstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.OrderItem;
import jakarta.transaction.Transactional;
import java.sql.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test suite for order item persistence in the database.
 *
 * @author Cyrus Fung (cfung89)
 */
@SpringBootTest
class OrderItemRepositoryTests {
    @Autowired private OrderRepository orderRepository;
    private Order order;

    @Autowired private OrderItemRepository orderItemRepository;
    private OrderItem orderItem;

    @Autowired private ClothingItemRepository clothingItemRepository;
    private ClothingItem clothingItem;

    /**
     * Setup method for OrderItem.
     *
     * @author Cyrus Fung (cfung89)
     */
    @BeforeEach
    void setup() {
        order = createOrder();
        orderItem = createOrderItem();
        clothingItem = createClothingItem();

        orderItem.setClothingItem(clothingItem);
        orderItem.setOrder(order);
        orderItemRepository.save(orderItem);
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
     * Create a Order before each test.
     *
     * @author Cyrus Fung (cfung89)
     */
    private Order createOrder() {
        Order newOrder = new Order();
        newOrder.setPrice(49.99f);
        newOrder.setState(Order.State.PURCHASED);
        newOrder.setOrderDate(Date.valueOf("2024-01-15"));
        newOrder.setDeliveryDate(Date.valueOf("2024-02-08"));
        newOrder.setDeliveryAddress("123 Lim");

        return orderRepository.save(newOrder);
    }

    /**
     * Create a OrderItem before each test.
     *
     * @author Cyrus Fung (cfung89)
     */
    private OrderItem createOrderItem() {
        OrderItem newOrderItem = new OrderItem();
        newOrderItem.setQuantity(2);
        newOrderItem.setPurchasePrice(9.5f);

        return orderItemRepository.save(newOrderItem);
    }

    /**
     * Clears database after each test.
     *
     * @author Cyrus Fung (cfung89)
     */
    @AfterEach
    void clearDatabase() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        clothingItemRepository.deleteAll();
    }

    /**
     * Test retrieval of order item from database.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testPersisteAndLoadOrderItem() {
        OrderItem orderItemFromDb = orderItemRepository.findOrderItemById(orderItem.getId());
        assertNotNull(orderItemFromDb, "Could not find saved order item in database.");
    }

    /**
     * Test information of order item from database.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testReadOrderItemInfo() {
        // Read order item from database
        OrderItem orderItemFromDb = orderItemRepository.findOrderItemById(orderItem.getId());

        assertEquals(
                orderItem.getId(),
                orderItemFromDb.getId(),
                "Order item id was incorrectly saved in the database.");
        assertEquals(
                orderItem.getQuantity(),
                orderItemFromDb.getQuantity(),
                "Order item quantity was incorrectly saved in the database.");
        assertEquals(
                orderItem.getPurchasePrice(),
                orderItemFromDb.getPurchasePrice(),
                "Order item purchasePrice was incorrectly saved in the database.");
    }

    /**
     * Test associated order of order item from database is persisted correctly.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Transactional
    void testReadOrderAssociation() {
        // Read clothing item from database
        OrderItem orderItemFromDb = orderItemRepository.findOrderItemById(orderItem.getId());

        assertEquals(
                order.getId(),
                orderItemFromDb.getOrder().getId(),
                "OrderItem's Order was incorrectly saved in the database");
        assertEquals(
                order.getPrice(),
                orderItemFromDb.getOrder().getPrice(),
                "Order's price from OrderItem was incorrectly saved in the database");
        assertEquals(
                order.getState(),
                orderItemFromDb.getOrder().getState(),
                "Order's state from OrderItem was incorrectly saved in the database");
        assertEquals(
                order.getOrderDate(),
                orderItemFromDb.getOrder().getOrderDate(),
                "Order's orderDate from OrderItem was incorrectly saved in the database");
    }

    /**
     * Test associated order of order item from database is persisted correctly.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Transactional
    void testReadOrderAssociation2() {
        // Read clothing item from database
        OrderItem orderItemFromDb = orderItemRepository.findOrderItemById(orderItem.getId());

        assertEquals(
                order.getDeliveryDate(),
                orderItemFromDb.getOrder().getDeliveryDate(),
                "Order's deliveryDate from OrderItem was incorrectly saved in the database");
        assertEquals(
                order.getDeliveryAddress(),
                orderItemFromDb.getOrder().getDeliveryAddress(),
                "Order's deliveryAddress from OrderItem was incorrectly saved in the database");
        assertEquals(
                1,
                orderItemFromDb.getOrder().getItems().size(),
                "Order's length of items from OrderItem was incorrectly saved in the database");
    }

    /**
     * Test associated clothing item of order item from database is persisted correctly.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Transactional
    void testReadClothingItemAssociation() {
        // Read clothing item from database
        OrderItem orderItemFromDb = orderItemRepository.findOrderItemById(orderItem.getId());

        assertEquals(
                clothingItem.getId(),
                orderItemFromDb.getClothingItem().getId(),
                "Order item's clothingItem was incorrectly saved in the database");
        assertEquals(
                clothingItem.getSize(),
                orderItemFromDb.getClothingItem().getSize(),
                "ClothingItem's size from OrderItem was incorrectly saved in the database");
        assertEquals(
                clothingItem.getColour(),
                orderItemFromDb.getClothingItem().getColour(),
                "ClothingItem's colour from OrderItem was incorrectly saved in the database");
        assertEquals(
                clothingItem.getNumInStock(),
                orderItemFromDb.getClothingItem().getNumInStock(),
                "ClothingItem's numInStock from OrderItem was incorrectly saved in the database");
    }

    /**
     * Test deletion of order from database works.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testDeleteOrderItem() {
        // delete item in repository
        orderItemRepository.delete(orderItem);

        // Try to read clothing item from database
        OrderItem orderItemFromDb = orderItemRepository.findOrderItemById(orderItem.getId());

        // Assert clothing item not found
        assertNull(orderItemFromDb, "Order item was not successfully deleted in database.");

        // Order should not have been deleted
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertNotNull(orderFromDb, "Order incorrectly deleted from database.");

        // ClothingItem should not have been deleted
        ClothingItem clothingItemFromDb =
                clothingItemRepository.findClothingItemById(clothingItem.getId());
        assertNotNull(clothingItemFromDb, "ClothingItem incorrectly deleted from database.");
    }

    /**
     * Test updating numOfStock of order item from database is saved correctly.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testUpdateOrderItemQuantity() {
        int newQuantity = 100;
        orderItem.setQuantity(newQuantity);

        // Save updated order item from database
        orderItemRepository.save(orderItem);

        // Read updated order item from database
        OrderItem orderItemFromDb = orderItemRepository.findOrderItemById(orderItem.getId());

        // Assert correct responses
        assertEquals(
                newQuantity,
                orderItemFromDb.getQuantity(),
                "Order item quantity was incorrectly saved in the database.");
    }

    /**
     * Test updating colour of clothing item from database is saved correctly.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    void testUpdateOrderItemPurchasePrice() {
        float newPurchasePrice = 20.15f;
        orderItem.setPurchasePrice(newPurchasePrice);

        // Save updated clothing item from database
        orderItemRepository.save(orderItem);

        // Read updated clothing item from database
        OrderItem orderItemFromDb = orderItemRepository.findOrderItemById(orderItem.getId());

        // Assert correct responses
        assertEquals(
                newPurchasePrice,
                orderItemFromDb.getPurchasePrice(),
                "Order item colour was incorrectly saved in the database.");
    }
}
