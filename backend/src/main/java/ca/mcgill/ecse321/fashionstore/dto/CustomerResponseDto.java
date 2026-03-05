package ca.mcgill.ecse321.fashionstore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * DTO for Customer responses.
 *
 * @param id Customer ID.
 * @param email Customer email address.
 * @param address Customer address.
 * @param numOfLoyaltyPoints Customer number of loyalty points.
 * @param shoppingCartItems Customer list of shopping cart items.
 * @param purchasedOrders Customer list of purchased orders.
 */
public record CustomerResponseDto(
        @NotNull(message = "ID must not be null.") @Positive(message = "ID must be positive.") Integer id,
        @NotBlank(message = "Email is required.") @Email(message = "Email must be a valid email address.") String email,
        @NotBlank(message = "Address is required.") String address,
        @NotNull(message = "Number of loyalty points must not be null.") @PositiveOrZero(message = "Number of loyalty points must be positive or zero.") Integer numOfLoyaltyPoints,
        @NotNull(message = "List of shopping cart items must not be null.") List<@Valid ShoppingCartItemResponseDto> shoppingCartItems,
        @NotNull(message = "List of purchased orders must not be null.") List<@Valid OrderResponseDto> purchasedOrders) {

    /**
     * Constructor override for shoppingCartItems and purchasedOrders as they are mutable reference.
     */
    public CustomerResponseDto {
        shoppingCartItems =
                (shoppingCartItems == null) ? List.of() : List.copyOf(shoppingCartItems);
        purchasedOrders = (purchasedOrders == null) ? List.of() : List.copyOf(purchasedOrders);
    }

    @Override
    public List<ShoppingCartItemResponseDto> shoppingCartItems() {
        return (shoppingCartItems == null) ? null : List.copyOf(this.shoppingCartItems);
    }

    @Override
    public List<OrderResponseDto> purchasedOrders() {
        return (purchasedOrders == null) ? null : List.copyOf(this.purchasedOrders);
    }
}
