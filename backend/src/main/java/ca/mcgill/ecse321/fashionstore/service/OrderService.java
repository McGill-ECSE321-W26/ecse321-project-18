package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.OrderRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.OrderResponseDto;
import ca.mcgill.ecse321.fashionstore.dto.OrderStatusRequestDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.Order.State;
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
    private static final int CANCELLATION_HOURS_BEFORE_DELIVERY = 24;

    /**
     * Constructor for OrderService class
     *
     * @param customerRepository customer repository class
     * @param orderRepository order repository class
     * @param employeeRepository employee repository class
     * @author Flavie Qin, Aurore Zhang
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public OrderService(
            CustomerRepository customerRepository,
            OrderRepository orderRepository,
            EmployeeRepository employeeRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Service method to create a new order
     *
     * @param orderRequestDto Order Request DTO
     * @param customerId Customer ID of customer to associate order with
     * @return Order Response DTO
     * @author Flavie Qin
     */
    @Transactional
    public OrderResponseDto createOrder(@Valid OrderRequestDto orderRequestDto, int customerId) {
        Customer customer = Utils.findCustomerById(customerRepository, customerId);

        // create new order object
        Order newOrder = new Order();
        newOrder.setState(orderRequestDto.state());
        newOrder.setCustomer(customer);
        newOrder.setOrderDate(Date.valueOf(orderRequestDto.orderDate()));
        newOrder.setDeliveryDate(Date.valueOf(orderRequestDto.deliveryDate()));
        newOrder.setDeliveryAddress(orderRequestDto.deliveryAddress());
        newOrder.setPrice(orderRequestDto.price());

        // save object in database and return response
        this.orderRepository.save(newOrder);
        return new OrderResponseDto(newOrder);
    }

    /**
     * Service method to get all orders in the system
     *
     * @return List of Order Response DTOs
     * @author Flavie Qin
     */
    @Transactional
    public List<OrderResponseDto> getAllOrders() {
        List<OrderResponseDto> list = new ArrayList<>();

        // get all orders from database and save in list as response DTOs
        for (Order order : orderRepository.findAll()) {
            list.add(new OrderResponseDto(order));
        }

        return list;
    }

    /**
     * Service method to get all orders placed by a certain customer
     *
     * @param customerId Customer ID of customer to get orders from
     * @return List of Order Response DTOs
     * @author Flavie Qin
     */
    @Transactional
    public List<OrderResponseDto> getAllOrdersByCustomer(int customerId) {
        Customer customer = Utils.findCustomerById(customerRepository, customerId);

        List<OrderResponseDto> list = new ArrayList<>();

        // get all orders associated with a certain customer and save in list as response DTOs
        for (Order order : customer.getPurchasedOrders()) {
            list.add(new OrderResponseDto(order));
        }

        return list;
    }

    /**
     * Service method to update the status of an order.
     *
     * @param orderId Order ID to update.
     * @param orderStatusRequestDto OrderStatusRequestDto (state, employeeId).
     * @author Aurore Zhang
     */
    @Transactional
    public OrderResponseDto updateOrderStatus(
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
        OrderResponseDto dto = new OrderResponseDto(order);
        return dto;
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
        if (order.getState() == State.DELIVERED) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST, "Cannot cancel an order that is already delivered.");
        }
        long hoursUntilDelivery =
                ChronoUnit.HOURS.between(
                        java.time.LocalDateTime.now(),
                        order.getDeliveryDate().toLocalDate().atStartOfDay());
        if (hoursUntilDelivery < CANCELLATION_HOURS_BEFORE_DELIVERY) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST,
                    "Order can only be cancelled at least 24 hours before the delivery date.");
        }
    }
}
