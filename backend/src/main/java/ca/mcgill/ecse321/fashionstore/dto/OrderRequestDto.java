package ca.mcgill.ecse321.fashionstore.dto;

import ca.mcgill.ecse321.fashionstore.model.Order.State;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;

/**
 * DTO for Order requests.
 *
 * @param state Order state.
 * @param orderDate Order order date.
 * @param deliveryDate Order delivery date.
 * @param deliveryAddress Order delivery address.
 * @param price Price of order.
 * @author Cyrus Fung (cfung89)
 */
public record OrderRequestDto(
        @NotNull(message = "State must not be null.") State state,
        @NotNull(message = "Order date must not be null.") @PastOrPresent(message = "Order date must be today or before.") LocalDate orderDate,
        @NotNull(message = "Delivery date must not be null.") @FutureOrPresent(message = "Delivery date must be today or after.") LocalDate deliveryDate,
        @NotBlank(message = "Delivery address must not be blank.") String deliveryAddress,
        @NotNull(message = "Price must not be null.") @DecimalMin("0.01") Float price) {}
