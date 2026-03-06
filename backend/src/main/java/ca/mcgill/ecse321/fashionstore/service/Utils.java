package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Account;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.OrderItem;
import ca.mcgill.ecse321.fashionstore.model.Owner;
import ca.mcgill.ecse321.fashionstore.model.ShoppingCartItem;
import ca.mcgill.ecse321.fashionstore.repository.AccountRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderRepository;
import ca.mcgill.ecse321.fashionstore.repository.OwnerRepository;
import ca.mcgill.ecse321.fashionstore.repository.ShoppingCartItemRepository;
import java.util.Optional;
import org.springframework.http.HttpStatus;

/** Utility class for Service layer. */
class Utils {
    public static final String ID_NOT_NULL = "ID must not be null.";
    public static final String ID_POSITIVE = "ID must not be positive.";

    /**
     * Find Account by Account ID.
     *
     * @param accountRepository AccountRepository instance.
     * @param id Account ID.
     */
    public static Account findAccountById(AccountRepository accountRepository, int id) {
        if (id < 0) {
            throw new FashionStoreException(HttpStatus.BAD_REQUEST, Utils.ID_POSITIVE);
        }
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            throw new FashionStoreException(
                    HttpStatus.NOT_FOUND, String.format("Account ID %d was not found.", id));
        }
        return account.get();
    }

    /**
     * Find Owner by Owner ID.
     *
     * @param ownerRepository OwnerRepository instance.
     * @param id Owner ID.
     */
    public static Owner findOwnerById(OwnerRepository ownerRepository, int id) {
        if (id < 0) {
            throw new FashionStoreException(HttpStatus.BAD_REQUEST, Utils.ID_POSITIVE);
        }

        Optional<Owner> owner = ownerRepository.findById(id);
        if (owner.isEmpty()) {
            throw new FashionStoreException(
                    HttpStatus.NOT_FOUND, String.format("Owner ID %d was not found.", id));
        }
        return owner.get();
    }

    /**
     * Find Customer by Customer ID.
     *
     * @param customerRepository CustomerRepository instance.
     * @param id Customer ID.
     */
    public static Customer findCustomerById(CustomerRepository customerRepository, int id) {
        if (id < 0) {
            throw new FashionStoreException(HttpStatus.BAD_REQUEST, Utils.ID_POSITIVE);
        }

        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isEmpty()) {
            throw new FashionStoreException(
                    HttpStatus.NOT_FOUND, String.format("Customer ID %d was not found.", id));
        }
        return customer.get();
    }

    /**
     * Find Employee by Employee ID.
     *
     * @param employeeRepository EmployeeRepository instance.
     * @param id Employee ID.
     */
    public static Employee findEmployeeById(EmployeeRepository employeeRepository, int id) {
        if (id < 0) {
            throw new FashionStoreException(HttpStatus.BAD_REQUEST, Utils.ID_POSITIVE);
        }

        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isEmpty()) {
            throw new FashionStoreException(
                    HttpStatus.NOT_FOUND, String.format("Employee ID %d was not found.", id));
        }
        return employee.get();
    }

    /**
     * Find Order by Order ID.
     *
     * @param orderRepository OrderRepository instance.
     * @param id Order ID.
     */
    public static Order findOrderById(OrderRepository orderRepository, int id) {
        if (id < 0) {
            throw new FashionStoreException(HttpStatus.BAD_REQUEST, Utils.ID_POSITIVE);
        }

        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            throw new FashionStoreException(
                    HttpStatus.NOT_FOUND, String.format("Order ID %d was not found.", id));
        }
        return order.get();
    }

    /**
     * Find OrderItem by OrderItem ID.
     *
     * @param orderItemRepository OrderItemRepository instance.
     * @param id OrderItem ID.
     */
    public static OrderItem findOrderItemById(OrderItemRepository orderItemRepository, int id) {
        if (id < 0) {
            throw new FashionStoreException(HttpStatus.BAD_REQUEST, Utils.ID_POSITIVE);
        }

        Optional<OrderItem> orderItem = orderItemRepository.findById(id);
        if (orderItem.isEmpty()) {
            throw new FashionStoreException(
                    HttpStatus.NOT_FOUND, String.format("OrderItem ID %d was not found.", id));
        }
        return orderItem.get();
    }

    /**
     * Find ShoppingCartItem by ShoppingCartItem ID.
     *
     * @param shoppingCartItemRepository ShoppingCartItemRepository instance.
     * @param id ShoppingCartItem ID.
     */
    public static ShoppingCartItem findShoppingCartItemById(
            ShoppingCartItemRepository shoppingCartItemRepository, int id) {
        if (id < 0) {
            throw new FashionStoreException(HttpStatus.BAD_REQUEST, Utils.ID_POSITIVE);
        }

        Optional<ShoppingCartItem> shoppingCartItem = shoppingCartItemRepository.findById(id);
        if (shoppingCartItem.isEmpty()) {
            throw new FashionStoreException(
                    HttpStatus.NOT_FOUND,
                    String.format("ShoppingCartItem ID %d was not found.", id));
        }
        return shoppingCartItem.get();
    }

    /**
     * Find ClothingProduct by ClothingProduct ID.
     *
     * @param clothingProductRepository ClothingProductRepository instance.
     * @param id ClothingProduct ID.
     */
    public static ClothingProduct findClothingProductById(
            ClothingProductRepository clothingProductRepository, int id) {
        Optional<ClothingProduct> clothingProduct = clothingProductRepository.findById(id);
        if (clothingProduct.isEmpty()) {
            throw new FashionStoreException(
                    HttpStatus.NOT_FOUND,
                    String.format("ClothingProduct ID %d was not found.", id));
        }
        return clothingProduct.get();
    }

    /**
     * Find ClothingItem by ClothingItem ID.
     *
     * @param clothingItemRepository ClothingItemRepository instance.
     * @param id ClothingItem ID.
     */
    public static ClothingItem findClothingItemById(
            ClothingItemRepository clothingItemRepository, int id) {
        if (id < 0) {
            throw new FashionStoreException(HttpStatus.BAD_REQUEST, Utils.ID_POSITIVE);
        }

        Optional<ClothingItem> clothingItem = clothingItemRepository.findById(id);
        if (clothingItem.isEmpty()) {
            throw new FashionStoreException(
                    HttpStatus.NOT_FOUND, String.format("ClothingItem ID %d was not found.", id));
        }
        return clothingItem.get();
    }
}
