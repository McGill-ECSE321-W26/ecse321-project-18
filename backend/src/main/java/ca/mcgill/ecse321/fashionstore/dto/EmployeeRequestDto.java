package ca.mcgill.ecse321.fashionstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * DTO for Employee requests.
 *
 * @param email Employee email address.
 * @param password Employee password.
 * @param address Employee address.
 * @param numOfLoyaltyPoints Employee number of loyalty points.
 */
public record EmployeeRequestDto(
        @NotBlank(message = "Email is required.") @Email(message = "Email must be a valid email address.") String email,
        @NotBlank(message = "Password is required.") @Size(min = 8, max = 32, message = "Password must be between 8 and 32 characters.") String password,
        @NotBlank(message = "Address is required.") String address,
        @NotNull(message = "Number of loyalty points must not be null.") @PositiveOrZero(message = "Number of loyalty points must be positive or zero.") Integer numOfLoyaltyPoints) {}
