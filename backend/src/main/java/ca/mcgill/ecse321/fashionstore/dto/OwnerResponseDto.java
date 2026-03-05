package ca.mcgill.ecse321.fashionstore.dto;

import ca.mcgill.ecse321.fashionstore.model.Owner;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for Owner responses.
 *
 * @param id Owner ID.
 * @param email Owner email address.
 */
public record OwnerResponseDto(
        @NotNull(message = "ID must not be null.") @Positive(message = "ID must be positive.") Integer id,
        @NotBlank(message = "Email is required.") @Email(message = "Email must be a valid email address.") String email) {

    /**
     * Constructor to map Owner to OwnerResponseDto.
     *
     * @param owner Owner instance.
     */
    public OwnerResponseDto(Owner owner) {
        this(owner.getId(), owner.getEmail());
    }
}
