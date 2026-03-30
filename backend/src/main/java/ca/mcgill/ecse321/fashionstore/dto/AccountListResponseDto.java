package ca.mcgill.ecse321.fashionstore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;

/**
 * DTO for Account List responses.
 *
 * @param owners List of all owners.
 * @param customers List of all customers.
 * @param employees List of all employees.
 */
@SuppressFBWarnings("EI_EXPOSE_REP")
public record AccountListResponseDto(
        @NotNull(message = "List of Owners must not be null.") @Size(min = 1, message = "Must have at least 1 owner.") List<@Valid OwnerResponseDto> owners,
        @NotNull(message = "List of Customers must not be null.") List<@Valid CustomerResponseDto> customers,
        @NotNull(message = "List of Employees must not be null.") List<@Valid EmployeeResponseDto> employees) {}
