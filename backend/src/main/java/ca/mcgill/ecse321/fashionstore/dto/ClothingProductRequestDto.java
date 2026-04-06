package ca.mcgill.ecse321.fashionstore.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for ClothingProduct requests.
 *
 * @param name ClothingProduct name.
 * @param price ClothingProduct price.
 * @param image ClothingProduct image.
 * @author Cyrus Fung (cfung89)
 */
public record ClothingProductRequestDto(
        @NotBlank(message = "Name must not be blank.") String name,
        @NotNull(message = "Price must not be null.") @DecimalMin("0.01") Float price,
        String image) {}
