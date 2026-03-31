package ca.mcgill.ecse321.fashionstore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for ShoppingCartItem requests.
 *
 * @param clothingItemId ClothingItem ID in shopping cart.
 * @param quantity Quantity of the ClothingItem in shopping cart.
 * @author Cyrus Fung (cfung89)
 */
public record ShoppingCartItemRequestDto(
        @NotNull(message = "ClothingItem ID must not be null.") @Positive(message = "ClothingItem ID must be positive.") Integer clothingItemId,
        @NotNull(message = "Quantity must not be null.") @Positive(message = "Quantity must be positive.") Integer quantity) {}
