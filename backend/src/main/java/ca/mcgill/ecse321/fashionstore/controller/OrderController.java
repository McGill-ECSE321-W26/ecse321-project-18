package ca.mcgill.ecse321.fashionstore.controller;

import ca.mcgill.ecse321.fashionstore.dto.OrderRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.OrderResponseDto;
import ca.mcgill.ecse321.fashionstore.dto.OrderStatusRequestDto;
import ca.mcgill.ecse321.fashionstore.service.OrderService;
import java.util.Collection;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Controller for Order REST API endpoints */
@RestController
@SuppressFBWarnings("SPRING_ENDPOINT")
public class OrderController {
    private OrderService orderService;

    /**
     * Constructor for OrderController
     *
     * @param orderService order service class
     * @author Flavie Qin
     */
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Creates a new order placed by a customer
     *
     * @param orderRequestDto the details of the new order to create
     * @param id customer id of the customer that is placing the order
     * @return a OrderResponseDto
     * @author Flavie Qin
     */
    @PostMapping("/fashionstore/account/customer/{id}/order")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto createOrder(
            @RequestBody OrderRequestDto orderRequestDto, @PathVariable int id) {
        return orderService.createOrder(orderRequestDto, id);
    }

    /**
     * Retrieves a list of all orders placed in the system
     *
     * @return a list of OrderResponseDto
     * @author Flavie Qin
     */
    @GetMapping("/fashionstore/order")
    public Collection<OrderResponseDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    /**
     * Retrieves a list of all orders placed by a certain customer
     *
     * @param id customer id of the customer
     * @return a list of OrderResponseDto
     * @author Flavie Qin
     */
    @GetMapping("/fashionstore/account/customer/{id}/order")
    public Collection<OrderResponseDto> getAllOrdersByCustomer(@PathVariable int id) {
        return orderService.getAllOrdersByCustomer(id);
    }

    /**
     * Updates the status of an order.
     *
     * @param id Order ID.
     * @param orderStatusRequestDto OrderStatusRequestDto (state, employeeId).
     * @return Returns the updated OrderResponseDto.
     * @author Aurore Zhang (ororio0)
     */
    @PutMapping("/fashionstore/order/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponseDto updateOrderStatus(
            @PathVariable int id, @RequestBody OrderStatusRequestDto orderStatusRequestDto) {
        return orderService.updateOrderStatus(id, orderStatusRequestDto);
    }
}
