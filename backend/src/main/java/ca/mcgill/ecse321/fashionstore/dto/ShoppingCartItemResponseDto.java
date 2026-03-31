package ca.mcgill.ecse321.fashionstore.dto;

import ca.mcgill.ecse321.fashionstore.model.ShoppingCartItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * DTO for ShoppingCartItem responses.
 *
 * @param id ShoppingCartItem ID.
 * @param clothingItem ClothingItemResponseDto object in shopping cart.
 * @param quantity Quantity of ClothingItem added to shopping cart.
 * @param customerId ShoppingCartItem customer ID. Can be null.
 * @author Cyrus Fung (cfung89)
 */
public record ShoppingCartItemResponseDto(
        @NotNull(message = "ID must not be null.") @Positive(message = "ID must be positive.") Integer id,
        @NotNull(message = "ClothingItemResponseDto must not be null.") @Valid ClothingItemResponseDto clothingItem,
        @NotNull(message = "Quantity must not be null.") @Positive(message = "Quantity must be positive.") Integer quantity,
        Integer customerId) {

    /**
     * Constructor to map ShoppingCartItem to ShoppingCartItemResponseDto.
     *
     * @param shoppingCartItem ShoppingCartItem instance.
     */
    public ShoppingCartItemResponseDto(ShoppingCartItem shoppingCartItem) {
        this(
                shoppingCartItem.getId(),
                new ClothingItemResponseDto(shoppingCartItem.getClothingItem()),
                shoppingCartItem.getQuantity(),
                shoppingCartItem.getCustomer().getId());
    }

    /**
     * Constructor to map ShoppingCartItems to ShoppingCartItemResponseDtos.
     *
     * @param shoppingCartItems List of ShoppingCartItem instances.
     */
    public static List<ShoppingCartItemResponseDto> shoppingCartItemResponseDtos(
            List<ShoppingCartItem> shoppingCartItems) {
        return shoppingCartItems.stream().map(ShoppingCartItemResponseDto::new).toList();
    }
}
