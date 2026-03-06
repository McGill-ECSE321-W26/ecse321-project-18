package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.OrderRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.OrderResponseDto;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/** Service class for Orders. */
@Service
@Validated
public class OrderService {
    private CustomerRepository customerRepository;
    private OrderRepository orderRepository;

    /**
     * Constructor for OrderService class
     *
     * @param customerRepository customer repository class
     * @param orderRepository order repository class
     * @author Flavie Qin
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public OrderService(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
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
}
