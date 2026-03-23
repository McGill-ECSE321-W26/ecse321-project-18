package ca.mcgill.ecse321.fashionstore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import ca.mcgill.ecse321.fashionstore.repository.AccountRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderRepository;
import ca.mcgill.ecse321.fashionstore.repository.OwnerRepository;
import ca.mcgill.ecse321.fashionstore.repository.ShoppingCartItemRepository;

@Service
@Validated
@Profile("dev")
public class UtilsService {
    private final AccountRepository accountRepository;
    private final ClothingItemRepository clothingItemRepository;
    private final ClothingProductRepository clothingProductRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final OwnerRepository ownerRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository;

    /**
     * UtilsService constructor.
     * 
     * @param accountRepository         AccountRepository required to access the
     *                                  database.
     * @param clothingItemRepository    ClothingItemRepository required to access
     *                                  the database.
     * @param clothingProductRepository ClothingProductRepository required to access
     *                                  the database.
     * @param customerRepository        CustomerRepository required to access the
     *                                  database.
     * @param employeeRepository        EmployeeRepository required to access the
     *                                  database.
     * @param orderItemRepository       OrderItemRepository required to access the
     *                                  database.
     * @param orderRepository           OrderRepository required to access the
     *                                  database.
     * @param ownerRepository           OwnerRepository required to access the
     *                                  database.
     * @author Cyrus Fung
     */
    @Autowired
    @SuppressWarnings("EI_EXPOSE_REP2")
    public UtilsService(
            AccountRepository accountRepository,
            ClothingItemRepository clothingItemRepository,
            ClothingProductRepository clothingProductRepository,
            CustomerRepository customerRepository,
            EmployeeRepository employeeRepository,
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository,
            OwnerRepository ownerRepository,
            ShoppingCartItemRepository shoppingCartItemRepository) {
        this.accountRepository = accountRepository;
        this.clothingItemRepository = clothingItemRepository;
        this.clothingProductRepository = clothingProductRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.ownerRepository = ownerRepository;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
            }

    /**
     * Generate random data.
     */
    public void generateRandomData() {
    }

    /**
     * Delete all data from database
     */
    public void deleteAllData() {
        accountRepository.deleteAll();
        clothingItemRepository.deleteAll();
        clothingProductRepository.deleteAll();
        customerRepository.deleteAll();
        employeeRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        ownerRepository.deleteAll();
        shoppingCartItemRepository.deleteAll();
    }
}
