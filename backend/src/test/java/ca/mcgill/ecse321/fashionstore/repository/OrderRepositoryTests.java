package ca.mcgill.ecse321.fashionstore.repository;

import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.Order.State;
import java.sql.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Kenneth
 * @summary Test suite for order persistence in the database.
 */
@SpringBootTest
class OrderRepositoryTests {

    @Autowired private OrderRepository orderRepository;

    private Order order;

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

    /** Clears database after each test. */
    @AfterEach
    void clearDatabase() {
        orderRepository.deleteAll();
    }

    /** Test retrieval of order from database is not null. */
    @Test
    void testPersistAndLoadOrder() {
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertNotNull(orderFromDb, "Could not find saved order in database.");
    }

    /** Test retrieval of order state is correct. */
    @Test
    void testPersistAndLoadOrderState() {
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertEquals(
                order.getState(), orderFromDb.getState(), "Order state is not saved in database.");
    }

    /** Test retrieval of order date is correct. */
    @Test
    void testPersistAndLoadOrderDate() {
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertEquals(
                order.getOrderDate(),
                orderFromDb.getOrderDate(),
                "Order date is not saved in database.");
    }

    /** Test retrieval of delivery date is correct. */
    @Test
    void testPersistAndLoadDeliveryDate() {
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertEquals(
                order.getDeliveryDate(),
                orderFromDb.getDeliveryDate(),
                "Delivery date is not saved in database.");
    }

    /** Test retrieval of delivery address is correct. */
    @Test
    void testPersistAndLoadDeliveryAddress() {
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertEquals(
                order.getDeliveryAddress(),
                orderFromDb.getDeliveryAddress(),
                "Delivery address is not saved in database.");
    }

    /** Test retrieval of order price is correct. */
    @Test
    void testPersistAndLoadOrderPrice() {
        Order orderFromDb = orderRepository.findOrderById(order.getId());
        assertEquals(
                order.getPrice(), orderFromDb.getPrice(), "Order price is not saved in database.");
    }

    /** Test updating an existing order in the database. */
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
        assertEquals(newAddress, updatedOrder.getDeliveryAddress(), "Order address did not update.");
        assertEquals(newPrice, updatedOrder.getPrice(), "Order price did not update.");
    }

    /** Test deleting an order from the database. */
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

}
