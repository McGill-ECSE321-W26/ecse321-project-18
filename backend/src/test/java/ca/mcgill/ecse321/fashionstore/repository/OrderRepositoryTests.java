package ca.mcgill.ecse321.fashionstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.Order.State;
import jakarta.transaction.Transactional;
import java.sql.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test suite for order persistence in the database.
 *
 * @author Kenneth Wang (KennethWang6)
 */
@SpringBootTest
class OrderRepositoryTests {

    @Autowired private OrderRepository orderRepository;
    @Autowired private ClothingItemRepository clothingItemRepository;
    @Autowired private ClothingProductRepository clothingProductRepository;

    private Order order;

    /**
     * Creates and saves an order before each test.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @BeforeEach
    void createOrder() {
        // Create order
        State state = State.PURCHASED;
        Date orderDate = Date.valueOf("2024-01-15");
        Date deliveryDate = Date.valueOf("2024-02-08");
        String deliveryAddress = "123 Lim";
        float price = 49.99f;

        Order newOrder = new Order();
        newOrder.setState(state);
        newOrder.setOrderDate(orderDate);
        newOrder.setDeliveryDate(deliveryDate);
        newOrder.setDeliveryAddress(deliveryAddress);
        newOrder.setPrice(price);

        // Save the order
        orderRepository.save(newOrder);
        order = newOrder;
    }

    /**
     * Clears database after each test.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @AfterEach
    void clearDatabase() {
        orderRepository.deleteAll();
        clothingItemRepository.deleteAll();
        clothingProductRepository.deleteAll();
    }

    /**
     * Test retrieval of order from database is not null.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void testPersistAndLoadOrder() {
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertNotNull(orderFromDb, "Could not find saved order in database.");
    }

    /**
     * Test retrieval of order state is correct.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void testPersistAndLoadOrderState() {
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertEquals(
                order.getState(), orderFromDb.getState(), "Order state is not saved in database.");
    }

    /**
     * Test retrieval of order date is correct.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void testPersistAndLoadOrderDate() {
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertEquals(
                order.getOrderDate(),
                orderFromDb.getOrderDate(),
                "Order date is not saved in database.");
    }

    /**
     * Test retrieval of delivery date is correct.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void testPersistAndLoadDeliveryDate() {
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertEquals(
                order.getDeliveryDate(),
                orderFromDb.getDeliveryDate(),
                "Delivery date is not saved in database.");
    }

    /**
     * Test retrieval of delivery address is correct.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void testPersistAndLoadDeliveryAddress() {
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertEquals(
                order.getDeliveryAddress(),
                orderFromDb.getDeliveryAddress(),
                "Delivery address is not saved in database.");
    }

    /**
     * Test retrieval of order price is correct.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void testPersistAndLoadOrderPrice() {
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertEquals(
                order.getPrice(), orderFromDb.getPrice(), "Order price is not saved in database.");
    }

    /**
     * Test updating an existing order in the database.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void testUpdateOrder() {
        // Retrieve the saved order
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertNotNull(orderFromDb, "Could not find saved order to update.");

        // Update fields
        State newState = State.ASSIGNED;
        String newAddress = "456 Updated Street";
        float newPrice = 79.99f;

        orderFromDb.setState(newState);
        orderFromDb.setDeliveryAddress(newAddress);
        orderFromDb.setPrice(newPrice);

        // Save updated order
        orderRepository.save(orderFromDb);

        // Retrieve again
        Order updatedOrder = orderRepository.findOrderById(order.getId());

        // Assert updates persisted
        assertEquals(newState, updatedOrder.getState(), "Order state did not update.");
        assertEquals(
                newAddress, updatedOrder.getDeliveryAddress(), "Order address did not update.");
        assertEquals(newPrice, updatedOrder.getPrice(), "Order price did not update.");
    }

    /**
     * Test deleting an order from the database.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void testDeleteOrder() {
        // Ensure it exists first
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertNotNull(orderFromDb, "Could not find saved order to delete.");

        // Delete the order
        orderRepository.delete(orderFromDb);

        // Try retrieving again
        Order deletedOrder = orderRepository.findOrderById(order.getId());

        // Assert deletion
        assertNull(deletedOrder, "Order was not deleted from the database.");
    }

    /**
     * Test that clothing items are associated with an order and not deleted when the order is
     * deleted.
     */
    @Transactional
    @Test
    void testOrderClothingItemAssociationAndNonCascadeDelete() {
        // Create clothing product
        String productName = "Crewneck";
        float productPrice = 59.99f;
        String productImage = "image.jpg";

        ClothingProduct product = new ClothingProduct();
        product.setName(productName);
        product.setPrice(productPrice);
        product.setImage(productImage);

        // Save product
        clothingProductRepository.save(product);

        // Create clothing item
        ClothingItem item = new ClothingItem();
        item.setClothingProduct(product);

        // Save item
        clothingItemRepository.save(item);

        // Associate item with order
        order.addItem(item);
        orderRepository.save(order);

        // Retrieve order and verify association
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertNotNull(orderFromDb, "Order not found in database.");
        assertEquals(
                1, orderFromDb.numberOfItems(), "Order should have exactly one clothing item.");
        assertEquals(
                item.getId(),
                orderFromDb.getItem(0).getId(),
                "Clothing item was not correctly associated with order.");

        // Delete order
        orderRepository.delete(orderFromDb);

        // Verify order is deleted
        Order deletedOrder = orderRepository.findOrderById(order.getId());
        assertNull(deletedOrder, "Order should be deleted but still exists.");

        // Verify clothing item still exists
        ClothingItem itemFromDb = clothingItemRepository.findClothingItemById(item.getId());
        assertNotNull(itemFromDb, "Clothing item should NOT be deleted when order is deleted.");

        // Verify clothing product still exists
        ClothingProduct productFromDb =
                clothingProductRepository.findClothingProductById(product.getId());
        assertNotNull(
                productFromDb, "Clothing product should NOT be deleted when order is deleted.");
    }
}
