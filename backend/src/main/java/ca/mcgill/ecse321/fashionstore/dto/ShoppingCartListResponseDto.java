package ca.mcgill.ecse321.fashionstore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;

/**
 * DTO for ShoppingCartItem responses.
 *
 * @param shoppingCartList List of ShoppingCartItemResponseDto objects in shopping cart.
 * @param price Total price of shopping cart.
 */
@SuppressWarnings("checkstyle:LineLength")
@SuppressFBWarnings("EI_EXPOSE_REP")
public record ShoppingCartListResponseDto(
        @NotNull(message = "ShoppingCartItemResponseDtos must not be null.") @Size(min = 0, message = "Invalid Shopping Cart state.") List<@Valid ShoppingCartItemResponseDto> shoppingCartList,
        @NotNull(message = "Price must not be null.") @DecimalMin("0.01") Float price) {}
