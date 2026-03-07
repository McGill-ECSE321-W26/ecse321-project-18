package ca.mcgill.ecse321.fashionstore.dto;

import ca.mcgill.ecse321.fashionstore.model.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for Account responses.
 *
 * @param id Account ID.
 * @param email Account email address.
 */
public record AccountResponseDto(
        @NotNull(message = "ID must not be null.") @Positive(message = "ID must be positive.") Integer id,
        @NotBlank(message = "Email is required.") @Email(message = "Email must be a valid email address.") String email) {

    /**
     * Constructor to map Account to AccountResponseDto.
     *
     * @param account Account instance.
     */
    public AccountResponseDto(Account account) {
        this(account.getId(), account.getEmail());
    }
}
