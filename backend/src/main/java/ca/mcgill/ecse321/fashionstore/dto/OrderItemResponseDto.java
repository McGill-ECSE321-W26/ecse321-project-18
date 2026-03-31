package ca.mcgill.ecse321.fashionstore.dto;

import ca.mcgill.ecse321.fashionstore.model.OrderItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * DTO for OrderItem responses.
 *
 * @param id OrderItem ID.
 * @param clothingItem ClothingItemResponseDto object in shopping cart.
 * @param quantity Quantity of ClothingItem added to shopping cart.
 * @param purchasePrice Price of one ClothingItem.
 * @param orderId OrderItem order ID. Can be null.
 * @author Cyrus Fung (cfung89)
 */
public record OrderItemResponseDto(
        @NotNull(message = "ID must not be null.") @Positive(message = "ID must be positive.") Integer id,
        @NotNull(message = "ClothingItemResponseDto must not be null.") @Valid ClothingItemResponseDto clothingItem,
        @NotNull(message = "Quantity must not be null.") @Positive(message = "Quantity must be positive.") Integer quantity,
        @NotNull(message = "Purchase price must not be null.") @DecimalMin("0.01") Float purchasePrice,
        Integer orderId) {

    /**
     * Constructor to map OrderItem to OrderItemResponseDto.
     *
     * @param orderItem OrderItem instance.
     * @author Cyrus Fung (cfung89)
     */
    public OrderItemResponseDto(OrderItem orderItem) {
        this(
                orderItem.getId(),
                new ClothingItemResponseDto(orderItem.getClothingItem()),
                orderItem.getQuantity(),
                orderItem.getPurchasePrice(),
                orderItem.getOrder().getId());
    }

    /**
     * Constructor to map OrderItems to OrderItemResponseDtos.
     *
     * @param orderItems List of OrderItem instances.
     * @author Cyrus Fung (cfung89)
     */
    public static List<OrderItemResponseDto> orderItemResponseDtos(List<OrderItem> orderItems) {
        return orderItems.stream().map(OrderItemResponseDto::new).toList();
    }
}
