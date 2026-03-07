package ca.mcgill.ecse321.fashionstore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO for the customer loyalty program, i.e. loyalty points.
 *
 * @param numOfLoyaltyPoints New number of loyalty points for the customer.
 */
public record LoyaltyPtsRequestDto(
        @NotNull(message = "Number of loyalty points must not be null.") @PositiveOrZero(message = "Number of loyalty points must be positive or zero.") Integer numOfLoyaltyPoints) {}
