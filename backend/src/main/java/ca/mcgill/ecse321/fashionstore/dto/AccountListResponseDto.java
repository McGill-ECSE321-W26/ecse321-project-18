package ca.mcgill.ecse321.fashionstore.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * DTO for Account List responses.
 *
 * @param owners List of all owners.
 * @param customers List of all customers.
 * @param employees List of all employees.
 * @author Cyrus Fung (cfung89)
 */
public record AccountListResponseDto(
        @NotNull(message = "List of Owners must not be null.") @Size(min = 1, message = "Must have at least 1 owner.") List<@Valid OwnerResponseDto> owners,
        @NotNull(message = "List of Customers must not be null.") List<@Valid CustomerResponseDto> customers,
        @NotNull(message = "List of Employees must not be null.") List<@Valid EmployeeResponseDto> employees) {

    /**
     * Constructor override as the class' attributes are mutable reference.
     *
     * @author Cyrus Fung (cfung89)
     */
    public AccountListResponseDto {
        owners = (owners == null) ? List.of() : List.copyOf(owners);
        customers = (customers == null) ? List.of() : List.copyOf(customers);
        employees = (employees == null) ? List.of() : List.copyOf(employees);
    }

    /**
     * Override to return immutable copy of the OwnerResponseDto List.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Override
    public List<OwnerResponseDto> owners() {
        return (owners == null) ? null : List.copyOf(this.owners);
    }

    /**
     * Override to return immutable copy of the CustomerResponseDto List.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Override
    public List<CustomerResponseDto> customers() {
        return (customers == null) ? null : List.copyOf(this.customers);
    }

    /**
     * Override to return immutable copy of the EmployeeResponseDto List.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Override
    public List<EmployeeResponseDto> employees() {
        return (employees == null) ? null : List.copyOf(this.employees);
    }
}
