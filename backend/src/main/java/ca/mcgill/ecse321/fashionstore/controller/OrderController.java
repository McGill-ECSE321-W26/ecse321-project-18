package ca.mcgill.ecse321.fashionstore.controller;

import ca.mcgill.ecse321.fashionstore.dto.OrderRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.OrderResponseDto;
import ca.mcgill.ecse321.fashionstore.dto.OrderStatusRequestDto;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.service.OrderService;
import ca.mcgill.ecse321.fashionstore.service.ShoppingCartItemService;
import java.util.ArrayList;
import java.util.List;
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
    private ShoppingCartItemService shoppingCartItemService;

    /**
     * Constructor for OrderController
     *
     * @param orderService order service class
     * @author Flavie Qin
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public OrderController(
            OrderService orderService, ShoppingCartItemService shoppingCartItemService) {
        this.orderService = orderService;
        this.shoppingCartItemService = shoppingCartItemService;
    }

    /**
     * Creates a new order placed by a customer and clears the customer's shopping cart.
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
        Order order = orderService.createOrder(orderRequestDto, id);
        shoppingCartItemService.deleteShoppingCartItems(id);
        return new OrderResponseDto(order);
    }

    /**
     * Retrieves a list of all orders placed in the system
     *
     * @return a list of OrderResponseDto
     * @author Flavie Qin
     */
    @GetMapping("/fashionstore/order")
    public List<OrderResponseDto> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<OrderResponseDto> orderDtos = new ArrayList<>();
        for (Order order : orders) {
            orderDtos.add(new OrderResponseDto(order));
        }

        return orderDtos;
    }

    /**
     * Retrieves a list of all orders placed by a certain customer
     *
     * @param id customer id of the customer
     * @return a list of OrderResponseDto
     * @author Flavie Qin
     */
    @GetMapping("/fashionstore/account/customer/{id}/order")
    public List<OrderResponseDto> getAllOrdersByCustomer(@PathVariable int id) {
        List<Order> orders = orderService.getAllOrdersByCustomer(id);

        List<OrderResponseDto> orderDtos = new ArrayList<>();
        for (Order order : orders) {
            orderDtos.add(new OrderResponseDto(order));
        }

        return orderDtos;
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
        Order order = orderService.updateOrderStatus(id, orderStatusRequestDto);
        return new OrderResponseDto(order);
    }
}
