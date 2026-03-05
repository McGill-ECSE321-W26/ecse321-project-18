package ca.mcgill.ecse321.fashionstore.dto;

import ca.mcgill.ecse321.fashionstore.model.Order.State;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for Order responses.
 *
 * @param state Order state.
 * @param orderDate Order order date.
 * @param deliveryDate Order delivery date.
 * @param deliveryAddress Order delivery address.
 * @param price Price of order.
 * @param orderItems Items in order.
 * @param customerId ID of customer who made the order. Can be null.
 * @param employeeId ID of employee who is assigned to the order. Can be null.
 */
public record OrderResponseDto(
        @NotNull(message = "ID must not be null.") @Positive(message = "ID must be positive.") Integer id,
        @NotNull(message = "State must not be null.") State state,
        @NotNull(message = "Order date must not be null.") @PastOrPresent(message = "Order date must be today or before.") LocalDate orderDate,
        @NotNull(message = "Delivery date must not be null.") @Future(message = "Delivery date must be today or before.") LocalDate deliveryDate,
        @NotBlank(message = "Delivery address must not be blank.") String deliveryAddress,
        @NotNull(message = "Price must not be null.") @DecimalMin("0.01") Float price,
        @NotNull(message = "List of OrderItems must not be null.") List<@Valid OrderItemResponseDto> orderItems,
        Integer customerId,
        Integer employeeId) {

    /** Constructor override for orderItems and purchasedOrders as it sets a mutable reference. */
    public OrderResponseDto {
        orderItems = (orderItems == null) ? List.of() : List.copyOf(orderItems);
    }

    @Override
    public List<OrderItemResponseDto> orderItems() {
        return (orderItems == null) ? null : List.copyOf(this.orderItems);
    }
}
