package ca.mcgill.ecse321.fashionstore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for ShoppingCartItem responses.
 *
 * @param id ShoppingCartItem ID.
 * @param clothingItem ClothingItemResponseDto object in shopping cart.
 * @param quantity Quantity of ClothingItem added to shopping cart.
 * @param customerId ShoppingCartItem customer ID. Can be null.
 */
public record ShoppingCartItemResponseDto(
        @NotNull(message = "ID must not be null.") @Positive(message = "ID must be positive.") Integer id,
        @NotNull(message = "ClothingItemResponseDto must not be null.") @Valid ClothingItemResponseDto clothingItem,
        @NotNull(message = "Quantity must not be null.") @Positive(message = "Quantity must be positive.") Integer quantity,
        Integer customerId) {}
