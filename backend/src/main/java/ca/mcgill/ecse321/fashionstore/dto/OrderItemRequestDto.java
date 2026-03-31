package ca.mcgill.ecse321.fashionstore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for OrderItem requests.
 *
 * @param clothingItemId ClothingItem ID in order.
 * @param quantity Quantity of the ClothingItem in order.
 * @param purchasePrice Price of one ClothingItem.
 * @author Cyrus Fung (cfung89)
 */
public record OrderItemRequestDto(
        @NotNull(message = "ClothingItem ID must not be null.") @Positive(message = "ClothingItem ID must be positive.") Integer clothingItemId,
        @NotNull(message = "Quantity must not be null.") @Positive(message = "Quantity must be positive.") Integer quantity,
        @NotNull(message = "Purchase price must not be null.") @DecimalMin("0.01") Float purchasePrice) {}
