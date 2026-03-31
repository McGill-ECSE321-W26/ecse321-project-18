package ca.mcgill.ecse321.fashionstore.dto;

import ca.mcgill.ecse321.fashionstore.model.Employee;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * DTO for Employee responses.
 *
 * @param id Employee ID.
 * @param email Employee email address.
 * @param address Employee address.
 * @param numOfLoyaltyPoints Employee number of loyalty points.
 * @param shoppingCartItems Employee list of shopping cart items.
 * @param purchasedOrders Employee list of purchased orders.
 * @param assignedOrders Employee list of assigned orders.
 * @author Cyrus Fung (cfung89)
 */
public record EmployeeResponseDto(
        @NotNull(message = "ID must not be null.") @Positive(message = "ID must be positive.") Integer id,
        @NotBlank(message = "Email is required.") @Email(message = "Email must be a valid email address.") String email,
        @NotBlank(message = "Address is required.") String address,
        @NotNull(message = "Number of loyalty points must not be null.") @PositiveOrZero(message = "Number of loyalty points must be positive or zero.") Integer numOfLoyaltyPoints,
        @NotNull(message = "List of ShoppingCartItems must not be null.") List<@Valid ShoppingCartItemResponseDto> shoppingCartItems,
        @NotNull(message = "List of purchased orders must not be null.") List<@Valid OrderResponseDto> purchasedOrders,
        @NotNull(message = "List of assigned orders must not be null.") List<@Valid OrderResponseDto> assignedOrders) {

    /**
     * Constructor override for shoppingCartItems, purchasedOrders, and assignedOrders as they set
     * mutable references.
     *
     * @author Cyrus Fung (cfung89)
     */
    public EmployeeResponseDto {
        shoppingCartItems =
                (shoppingCartItems == null) ? List.of() : List.copyOf(shoppingCartItems);
        purchasedOrders = (purchasedOrders == null) ? List.of() : List.copyOf(purchasedOrders);
        assignedOrders = (assignedOrders == null) ? List.of() : List.copyOf(assignedOrders);
    }

    /**
     * Constructor to map Employee to EmployeeResponseDto.
     *
     * @param employee Employee instance.
     * @author Cyrus Fung (cfung89)
     */
    public EmployeeResponseDto(Employee employee) {
        this(
                employee.getId(),
                employee.getEmail(),
                employee.getAddress(),
                employee.getNumLoyaltyPoints(),
                employee.getShoppingCartItems().stream()
                        .map(ShoppingCartItemResponseDto::new)
                        .toList(),
                employee.getPurchasedOrders().stream().map(OrderResponseDto::new).toList(),
                employee.getAssignedOrders().stream().map(OrderResponseDto::new).toList());
    }

    /**
     * Constructor to map Employees to a list of EmployeeResponseDtos.
     *
     * @param shoppingCartItems List of ShoppingCartItem instances.
     * @author Cyrus Fung (cfung89)
     */
    public static List<EmployeeResponseDto> employeeResponseDtos(List<Employee> employees) {
        return employees.stream().map(EmployeeResponseDto::new).toList();
    }

    /**
     * Override to return immutable copy of the OrderResponseDto List.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Override
    public List<ShoppingCartItemResponseDto> shoppingCartItems() {
        return (shoppingCartItems == null) ? null : List.copyOf(this.shoppingCartItems);
    }

    /**
     * Override to return immutable copy of the OrderResponseDto List.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Override
    public List<OrderResponseDto> purchasedOrders() {
        return (purchasedOrders == null) ? null : List.copyOf(this.purchasedOrders);
    }

    /**
     * Override to return immutable copy of the OrderResponseDto List.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Override
    public List<OrderResponseDto> assignedOrders() {
        return (assignedOrders == null) ? null : List.copyOf(this.assignedOrders);
    }
}
