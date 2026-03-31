package ca.mcgill.ecse321.fashionstore.dto;

import ca.mcgill.ecse321.fashionstore.model.Order.State;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for Order status update requests.
 *
 * @param state New order state.
 * @param employeeId ID of the employee performing the action.
 * @author Aurore Zhang (ororio0)
 */
public record OrderStatusRequestDto(
        @NotNull(message = "State must not be null.") State state,
        @NotNull(message = "Employee ID must not be null.") @Positive(message = "Employee ID must be positive.") Integer employeeId) {}
