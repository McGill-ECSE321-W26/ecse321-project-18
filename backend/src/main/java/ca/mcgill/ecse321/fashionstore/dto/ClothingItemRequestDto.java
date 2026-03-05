package ca.mcgill.ecse321.fashionstore.dto;

import ca.mcgill.ecse321.fashionstore.model.ClothingItem.Colour;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * DTO for ClothingItem requests.
 *
 * @param size ClothingItem size.
 * @param colour ClothingItem colour.
 * @param numInStock Number of items in stock of the ClothingItem.
 * @param clothingProductId ID of the ClothingProduct.
 */
public record ClothingItemRequestDto(
        @NotNull(message = "Size must not be null.") Size size,
        @NotNull(message = "Colour must not be null.") Colour colour,
        @NotNull(message = "Stock must not be null.") @PositiveOrZero(message = "Stock must be positive or zero.") Integer numInStock,
        @NotNull(message = "ClothingProduct ID must not be null.") @Positive(message = "ClothingProduct ID must be positive.") Integer clothingProductId) {}
