package ca.mcgill.ecse321.fashionstore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for OrderItem responses.
 *
 * @param id OrderItem ID.
 * @param clothingItem ClothingItemResponseDto object in shopping cart.
 * @param quantity Quantity of ClothingItem added to shopping cart.
 * @param purchasePrice Price of one ClothingItem.
 * @param orderId OrderItem order ID. Can be null.
 */
public record OrderItemResponseDto(
        @NotNull(message = "ID must not be null.") @Positive(message = "ID must be positive.") Integer id,
        @NotNull(message = "ClothingItemResponseDto must not be null.") @Valid ClothingItemResponseDto clothingItem,
        @NotNull(message = "Quantity must not be null.") @Positive(message = "Quantity must be positive.") Integer quantity,
        @NotNull(message = "Purchase price must not be null.") @DecimalMin("0.01") Float purchasePrice,
        Integer orderId) {}
