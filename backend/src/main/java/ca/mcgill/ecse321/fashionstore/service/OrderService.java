package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.OrderRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.OrderStatusRequestDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.Order.State;
import ca.mcgill.ecse321.fashionstore.model.OrderItem;
import ca.mcgill.ecse321.fashionstore.model.ShoppingCartItem;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.sql.Date;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/** Service class for Orders. */
@Service
@Validated
public class OrderService {
    private CustomerRepository customerRepository;
    private OrderRepository orderRepository;
    private EmployeeRepository employeeRepository;
    private ClothingItemRepository clothingItemRepository;
    private static final int CANCELLATION_HOURS_BEFORE_DELIVERY = 24;

    /**
     * Constructor for OrderService class
     *
     * @param customerRepository customer repository class
     * @param orderRepository order repository class
     * @param employeeRepository employee repository class
     * @author Flavie Qin (flavieq88)
     * @author Aurore Zhang (ororio0)
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public OrderService(
            CustomerRepository customerRepository,
            OrderRepository orderRepository,
            EmployeeRepository employeeRepository,
            ClothingItemRepository clothingItemRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.employeeRepository = employeeRepository;
        this.clothingItemRepository = clothingItemRepository;
    }

    /**
     * Service method to create a new order
     *
     * @param orderRequestDto Order Request DTO
     * @param customerId Customer ID of customer to associate order with
     * @return new Order instance
     * @author Flavie Qin (flavieq88)
     */
    @Transactional
    public Order createOrder(@Valid OrderRequestDto orderRequestDto, int customerId) {
        validateOrderRequest(orderRequestDto);
        Customer customer = Utils.findCustomerById(customerRepository, customerId);

        // create new order object
        Order newOrder = new Order();
        newOrder.setState(orderRequestDto.state());
        newOrder.setCustomer(customer);
        newOrder.setOrderDate(Date.valueOf(orderRequestDto.orderDate()));
        newOrder.setDeliveryDate(Date.valueOf(orderRequestDto.deliveryDate()));
        newOrder.setDeliveryAddress(orderRequestDto.deliveryAddress());
        newOrder.setPrice(orderRequestDto.price());

        // assign all items in customer cart to the order
        List<ClothingItem> items = assignOrderItems(newOrder, customer);

        // save updated objects in database and return order
        this.orderRepository.save(newOrder);
        this.clothingItemRepository.saveAll(items);
        return newOrder;
    }

    /**
     * Validate that the delivery date and order date in the OrderRequestDto instance are valid.
     *
     * @param orderRequestDto OrderRequestDto instance
     * @author Flavie Qin (flavieq88)
     */
    private void validateOrderRequest(OrderRequestDto orderRequestDto) {
        if (orderRequestDto.orderDate().plusDays(1).isAfter(orderRequestDto.deliveryDate())) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST,
                    "Delivery date must be at least 24 hours after order date.");
        }
    }

    /**
     * Helper method to assign all items from a customer's shopping cart to the new order
     *
     * @param order Order instance
     * @param customer Customer instance that is placing the order
     * @return List of clothing items with updated num in stock
     * @author Flavie Qin (flavieq88)
     */
    private List<ClothingItem> assignOrderItems(Order order, Customer customer) {
        // throw error if no items in shopping cart
        if (customer.getShoppingCartItems().isEmpty()) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST, "Cannot create a new order for no items.");
        }

        List<ClothingItem> clothingItems = new ArrayList<>();
        // go through customer shopping cart items and add to the order
        for (ShoppingCartItem shoppingCartItem : customer.getShoppingCartItems()) {
            validateItemQuantities(shoppingCartItem);
            // create order item
            OrderItem newItem = createNewOrderItem(shoppingCartItem);
            order.addItem(newItem);
            // update num in stock of clothing item and add to list to save
            int newNumInStock = newItem.getClothingItem().getNumInStock() - newItem.getQuantity();
            newItem.getClothingItem().setNumInStock(newNumInStock);
            clothingItems.add(newItem.getClothingItem());
        }
        return clothingItems;
    }

    private void validateItemQuantities(ShoppingCartItem shoppingCartItem) {
        if (shoppingCartItem.getQuantity() > shoppingCartItem.getClothingItem().getNumInStock()) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST,
                    String.format(
                            "Clothing item %s does not have enough quantity in stock.",
                            shoppingCartItem.getClothingItem().getClothingProduct().getName()));
        }
    }

    private OrderItem createNewOrderItem(ShoppingCartItem shoppingCartItem) {
        ClothingItem clothingItem = shoppingCartItem.getClothingItem();
        OrderItem newItem = new OrderItem();
        newItem.setPurchasePrice(clothingItem.getClothingProduct().getPrice());
        newItem.setClothingItem(clothingItem);
        newItem.setQuantity(shoppingCartItem.getQuantity());
        return newItem;
    }

    /**
     * Service method to get all orders in the system
     *
     * @return List of Order
     * @author Flavie Qin (flavieq88)
     */
    @Transactional
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();

        // get all orders from database and save in list
        for (Order order : orderRepository.findAll()) {
            list.add(order);
        }

        return list;
    }

    /**
     * Service method to get all orders placed by a certain customer
     *
     * @param customerId Customer ID of customer to get orders from
     * @return List of Order
     * @author Flavie Qin (flavieq88)
     */
    @Transactional
    public List<Order> getAllOrdersByCustomer(int customerId) {
        Customer customer = Utils.findCustomerById(customerRepository, customerId);

        // get all orders associated with a certain customer and return in list
        return customer.getPurchasedOrders();
    }

    /**
     * Service method to update the status of an order.
     *
     * @param orderId Order ID to update.
     * @param orderStatusRequestDto OrderStatusRequestDto (state, employeeId).
     * @return update Order instance
     * @author Aurore Zhang
     */
    @Transactional
    public Order updateOrderStatus(
            int orderId, @Valid OrderStatusRequestDto orderStatusRequestDto) {
        Order order = Utils.findOrderById(orderRepository, orderId);
        Employee employee =
                Utils.findEmployeeById(employeeRepository, orderStatusRequestDto.employeeId());
        State newState = orderStatusRequestDto.state();
        if (newState == State.ASSIGNED) {
            validateAndAssign(order, employee);
        } else if (newState == State.PREPARED) {
            validatePrepared(order);
        } else if (newState == State.CANCELLED) {
            validateCancelled(order);
        } else {
            validateTransition(newState);
        }
        order.setState(newState);
        order = orderRepository.save(order);
        return order;
    }

    /**
     * Validates that a state transition is valid.
     *
     * @param newState New state to transition to.
     * @author Aurore Zhang
     */
    private void validateTransition(State newState) {
        throw new FashionStoreException(
                HttpStatus.BAD_REQUEST,
                String.format("Invalid status transition to %s.", newState));
    }

    /**
     * Validates and assigns an employee to an order.
     *
     * @param order Order to assign.
     * @param employee Employee to assign to the order.
     * @author Aurore Zhang
     */
    private void validateAndAssign(Order order, Employee employee) {
        if (order.getState() != State.PURCHASED) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST, "Order must be in purchased state to be assigned.");
        }
        if (order.getCustomer() != null && order.getCustomer().getId() == employee.getId()) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST,
                    "Employees cannot assign themselves to their own order.");
        }
        order.setEmployee(employee);
    }

    /**
     * Validates that an order can be marked as prepared.
     *
     * @param order Order to prepare.
     * @author Aurore Zhang
     */
    private void validatePrepared(Order order) {
        if (order.getState() != State.ASSIGNED) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST,
                    "Order must be in assigned state to be marked as prepared.");
        }
    }

    /**
     * Validates that an order can be cancelled.
     *
     * @param order Order to cancel.
     * @author Aurore Zhang
     */
    private void validateCancelled(Order order) {
        State state = order.getState();
        if (state == State.CANCELLED) {
            return;
        }
        if (state == State.DELIVERED) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST, "Cannot cancel an order that is delivered.");
        }
        validateCancellationTime(order);
    }

    /**
     * Validates that an order is being cancelled at least 24 hours before delivery.
     *
     * @param order Order to cancel.
     * @author Aurore Zhang
     */
    private void validateCancellationTime(Order order) {
        long hoursUntilDelivery =
                ChronoUnit.HOURS.between(
                        java.time.LocalDateTime.now(),
                        order.getDeliveryDate().toLocalDate().atStartOfDay());
        if (hoursUntilDelivery < CANCELLATION_HOURS_BEFORE_DELIVERY) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST, "Cannot cancel order within 24 hours of delivery.");
        }
    }
}
