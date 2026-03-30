package ca.mcgill.ecse321.fashionstore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for ShoppingCartItem responses.
 *
 * @param shoppingCartItem ShoppingCartItemResponseDto object in shopping cart.
 * @param price Total price of shopping cart.
 */
public record ShoppingCartResponseDto(
        @Valid ShoppingCartItemResponseDto shoppingCartItem,
        @NotNull(message = "Price must not be null.") @DecimalMin("0.01") Float price) {}
