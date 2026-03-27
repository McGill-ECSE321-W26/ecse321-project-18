package ca.mcgill.ecse321.fashionstore.dto;

import ca.mcgill.ecse321.fashionstore.model.Account;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for Account responses.
 *
 * @param id Account ID.
 * @param email Account email address.
 * @param accountType Account type.
 */
public record AccountResponseDto(
        @NotNull(message = "ID must not be null.") @Positive(message = "ID must be positive.") Integer id,
        @NotBlank(message = "Email is required.") @Email(message = "Email must be a valid email address.") String email,
        @NotBlank(message = "Account type must not be blank.") AccountType accountType) {

    /** Enum for account type */
    public enum AccountType {
        @JsonProperty("Owner")
        OWNER,
        @JsonProperty("Employee")
        EMPLOYEE,
        @JsonProperty("Customer")
        CUSTOMER,
    }

    /**
     * Constructor to map Account to AccountResponseDto.
     *
     * @param account Account instance.
     */
    public AccountResponseDto(Account account) {
        this(account.getId(), account.getEmail(), null);
    }
}
