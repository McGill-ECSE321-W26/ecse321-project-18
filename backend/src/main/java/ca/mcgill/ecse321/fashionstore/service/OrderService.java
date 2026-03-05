package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.OrderRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.OrderResponseDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
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

    /**
     * Constructor for OrderService class
     *
     * @param customerRepository customer repository class
     * @param orderRepository order repository class
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
     */
    @Transactional
    public OrderResponseDto createOrder(@Valid OrderRequestDto orderRequestDto, int customerId) {
        // validate that customer with this id exists
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            throw new FashionStoreException(
                    HttpStatus.NOT_FOUND,
                    String.format("Customer ID %d was not found.", customerId));
        }
        // create new order object
        Order newOrder = new Order();
        newOrder.setState(orderRequestDto.state());
        newOrder.setCustomer(customerOptional.get());
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
     */
    @Transactional
    public Collection<OrderResponseDto> getAllOrders() {
        Collection<OrderResponseDto> list = new ArrayList<>();

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
     */
    @Transactional
    public Collection<OrderResponseDto> getAllOrdersByCustomer(int customerId) {
        // validate that customer with this id exists
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isEmpty()) {
            throw new FashionStoreException(
                    HttpStatus.NOT_FOUND,
                    String.format("Customer ID %d was not found.", customerId));
        }

        Collection<OrderResponseDto> list = new ArrayList<>();

        // get all orders associated with a certain customer and save in list as response DTOs
        for (Order order : customerOptional.get().getPurchasedOrders()) {
            list.add(new OrderResponseDto(order));
        }

        return list;
    }
}
