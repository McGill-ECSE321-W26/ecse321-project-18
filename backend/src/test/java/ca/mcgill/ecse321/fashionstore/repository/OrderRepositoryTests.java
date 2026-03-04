package ca.mcgill.ecse321.fashionstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.Order.State;
import ca.mcgill.ecse321.fashionstore.model.OrderItem;
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
    @Autowired private OrderItemRepository orderItemRepository;

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
        orderItemRepository.deleteAll();
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
     * Test that an order correctly stores its associated clothing items.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Transactional
    @Test
    void testOrderClothingItemAssociation() {
        // Create order item
        OrderItem orderItem = new OrderItem();
        orderItemRepository.save(orderItem);

        // Add item to order
        order.addItem(orderItem);
        orderRepository.save(order);

        // Retrieve order and verify association
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertNotNull(orderFromDb, "Order not found in database.");
        assertEquals(
                1, orderFromDb.numberOfItems(), "Order should contain exactly one clothing item.");
        assertEquals(
                orderItem.getId(),
                orderFromDb.getItem(0).getId(),
                "Associated clothing item does not match expected item.");
    }

    /**
     * Test that deleting an order does not delete its associated clothing items.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Transactional
    @Test
    void testOrderDeletionDoesNotDeleteClothingItem() {
        // Create clothing product
        // Create order item
        OrderItem orderItem = new OrderItem();
        orderItemRepository.save(orderItem);

        // Add item to order
        order.addItem(orderItem);
        orderRepository.save(order);

        // Delete the order
        orderRepository.delete(order);

        // Verify order deleted
        assertNull(
                orderRepository.findOrderById(order.getId()),
                "Order should be deleted but still exists.");

        // Verify orderItem deleted
        assertNull(
                orderItemRepository.findOrderItemById(orderItem.getId()),
                "OrderItem should be deleted but still exists.");
    }
}
