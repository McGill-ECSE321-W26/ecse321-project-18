package ca.mcgill.ecse321.fashionstore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO for ClothingProduct requests.
 *
 * @param id ClothingProduct ID.
 * @param name ClothingProduct name.
 * @param price ClothingProduct price.
 * @param image ClothingProduct image.
 * @param clothingItems ClothingItems associated with the ClothingProduct.
 */
public record ClothingProductResponseDto(
        @NotNull(message = "ID must not be null.") @Positive(message = "ID must be positive.") Integer id,
        @NotBlank(message = "Name must not be blank.") String name,
        @NotNull(message = "Price must not be null.") @DecimalMin("0.01") Float price,
        @NotBlank(message = "Image must not be blank.") String image,
        @NotNull(message = "List of ClothingItems must not be null.") @Size(min = 1, message = "Order must have at least one item.") List<@Valid ClothingItemResponseDto> clothingItems) {

    /** Constructor override for clothingItems as it sets a mutable reference. */
    public ClothingProductResponseDto {
        clothingItems = (clothingItems == null) ? List.of() : List.copyOf(clothingItems);
    }

    @Override
    public List<ClothingItemResponseDto> clothingItems() {
        return (clothingItems == null) ? null : List.copyOf(this.clothingItems);
    }
}
