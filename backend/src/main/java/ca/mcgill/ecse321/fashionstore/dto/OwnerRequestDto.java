package ca.mcgill.ecse321.fashionstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for Owner requests.
 *
 * @param email Owner email address.
 * @param password Owner password.
 * @author Cyrus Fung (cfung89)
 */
public record OwnerRequestDto(
        @NotBlank(message = "Email is required.") @Email(message = "Email must be a valid email address.") String email,
        @NotBlank(message = "Password is required.") @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters.") String password) {}
