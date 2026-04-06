package ca.mcgill.ecse321.fashionstore.dto;

import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
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
 * @author Cyrus Fung (cfung89)
 */
public record ClothingProductResponseDto(
        @NotNull(message = "ID must not be null.") @Positive(message = "ID must be positive.") Integer id,
        @NotBlank(message = "Name must not be blank.") String name,
        @NotNull(message = "Price must not be null.") @DecimalMin("0.01") Float price,
        String image,
        @NotNull(message = "List of ClothingItems must not be null.") @Size(min = 1, message = "Order must have at least one item.") List<@Valid ClothingItemResponseDto> clothingItems) {

    /** Constructor override for clothingItems as it sets a mutable reference. */
    public ClothingProductResponseDto {
        clothingItems = (clothingItems == null) ? List.of() : List.copyOf(clothingItems);
    }

    /**
     * Constructor to map ClothingProduct to ClothingProductResponseDto.
     *
     * @param clothingProduct ClothingProduct instance.
     * @author Cyrus Fung (cfung89)
     */
    public ClothingProductResponseDto(ClothingProduct clothingProduct) {
        this(
                clothingProduct.getId(),
                clothingProduct.getName(),
                clothingProduct.getPrice(),
                clothingProduct.getImage(),
                clothingProduct.getItems().stream().map(ClothingItemResponseDto::new).toList());
    }

    @Override
    public List<ClothingItemResponseDto> clothingItems() {
        return (clothingItems == null) ? null : List.copyOf(this.clothingItems);
    }

    /**
     * Converts a list of ClothingProducts to a list of ClothingProductResponseDtos.
     *
     * @param clothingProducts the list of ClothingProducts to convert.
     * @return the list of converted ClothingProductResponseDtos.
     * @author Carolyn Wu (cw118)
     */
    public static List<ClothingProductResponseDto> clothingProductsToResponseDtos(
            List<ClothingProduct> clothingProducts) {
        return clothingProducts.stream().map(ClothingProductResponseDto::new).toList();
    }
}
