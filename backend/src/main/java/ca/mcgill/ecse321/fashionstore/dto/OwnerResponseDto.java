package ca.mcgill.ecse321.fashionstore.dto;

import ca.mcgill.ecse321.fashionstore.model.Owner;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * DTO for Owner responses.
 *
 * @param id Owner ID.
 * @param email Owner email address.
 * @author Cyrus Fung (cfung89)
 */
public record OwnerResponseDto(
        @NotNull(message = "ID must not be null.") @Positive(message = "ID must be positive.") Integer id,
        @NotBlank(message = "Email is required.") @Email(message = "Email must be a valid email address.") String email) {

    /**
     * Constructor to map Owner to OwnerResponseDto.
     *
     * @param owner Owner instance.
     * @author Cyrus Fung (cfung89)
     */
    public OwnerResponseDto(Owner owner) {
        this(owner.getId(), owner.getEmail());
    }

    /**
     * Constructor to map Owners to a list of OwnerResponseDtos.
     *
     * @param shoppingCartItems List of ShoppingCartItem instances.
     * @author Cyrus Fung (cfung89)
     */
    public static List<OwnerResponseDto> ownerResponseDtos(List<Owner> owners) {
        return owners.stream().map(OwnerResponseDto::new).toList();
    }
}
