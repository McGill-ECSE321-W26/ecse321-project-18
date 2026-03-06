package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.ShoppingCartItemRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.ShoppingCartItemResponseDto;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.ShoppingCartItem;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.ShoppingCartItemRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/** ShoppingCartItem Service class. */
@Service
@Validated
public class ShoppingCartItemService {
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final ClothingItemRepository clothingItemRepository;
    private final CustomerRepository customerRepository;

    /**
     * ShoppingCartItemService constructor.
     *
     * @param shoppingCartItemRepository ShoppingCartItemRepository required to access the database.
     * @param clothingItemRepository ClothingItemRepository required to access the database.
     * @param customerRepository CustomerRepository required to access the database.
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ShoppingCartItemService(
            ShoppingCartItemRepository shoppingCartItemRepository,
            ClothingItemRepository clothingItemRepository,
            CustomerRepository customerRepository) {
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.clothingItemRepository = clothingItemRepository;
        this.customerRepository = customerRepository;
    }

    /**
     * Service method to get all the shopping cart items of a customer.
     *
     * @param customerId Customer ID.
     */
    public List<ShoppingCartItemResponseDto> getShoppingCartItems(
            @NotNull(message = Utils.ID_NOT_NULL) @Positive(message = Utils.ID_POSITIVE) int customerId) {
        Customer customer = Utils.findCustomerById(customerRepository, customerId);
        List<ShoppingCartItemResponseDto> shoppingCartItems =
                ShoppingCartItemResponseDto.shoppingCartItemResponseDtos(
                        customer.getShoppingCartItems());
        return shoppingCartItems;
    }

    /**
     * Service method to add a new shopping cart item to a customer.
     *
     * @param customerId Customer ID.
     * @param shoppingCartItemRequestDto ShoppingCartItem request DTO.
     */
    @Transactional
    public ShoppingCartItemResponseDto addShoppingCartItem(
            @NotNull(message = Utils.ID_NOT_NULL) @Positive(message = Utils.ID_POSITIVE) int customerId,
            @Valid ShoppingCartItemRequestDto shoppingCartItemRequestDto) {
        ShoppingCartItem newShoppingCartItem = new ShoppingCartItem();
        int clothingItemId = shoppingCartItemRequestDto.clothingItemId();
        ClothingItem clothingItem =
                Utils.findClothingItemById(clothingItemRepository, clothingItemId);
        newShoppingCartItem.setClothingItem(clothingItem);
        newShoppingCartItem.setQuantity(shoppingCartItemRequestDto.quantity());
        Customer customer = Utils.findCustomerById(customerRepository, customerId);
        newShoppingCartItem.setCustomer(customer);
        newShoppingCartItem = shoppingCartItemRepository.save(newShoppingCartItem);
        ShoppingCartItemResponseDto dto = new ShoppingCartItemResponseDto(newShoppingCartItem);
        return dto;
    }

    /**
     * Service method to update a customer's.
     *
     * @param id ShoppingCartItem ID to be updated.
     * @param shoppingCartItemRequestDto ShoppingCartItem request DTO.
     */
    @Transactional
    public ShoppingCartItemResponseDto updateShoppingCartItem(
            @NotNull(message = Utils.ID_NOT_NULL) @Positive(message = Utils.ID_POSITIVE) int id,
            @Valid ShoppingCartItemRequestDto shoppingCartItemRequestDto) {
        ShoppingCartItem shoppingCartItem =
                Utils.findShoppingCartItemById(shoppingCartItemRepository, id);
        shoppingCartItem.setQuantity(shoppingCartItemRequestDto.quantity());
        shoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem);
        ShoppingCartItemResponseDto dto = new ShoppingCartItemResponseDto(shoppingCartItem);
        return dto;
    }

    /**
     * Service method to delete a new shopping cart item to a customer.
     *
     * @param id ShoppingCartItem ID to be deleted.
     */
    @Transactional
    public void deleteShoppingCartItem(
            @NotNull(message = Utils.ID_NOT_NULL) @Positive(message = Utils.ID_POSITIVE) int id) {
        shoppingCartItemRepository.deleteById(id);
    }

    /**
     * Service method to delete all shopping cart items of a customer.
     *
     * @param customerId Customer ID.
     */
    @Transactional
    public void deleteShoppingCartItems(
            @NotNull(message = Utils.ID_NOT_NULL) @Positive(message = Utils.ID_POSITIVE) int customerId) {
        Customer customer = Utils.findCustomerById(customerRepository, customerId);
        for (ShoppingCartItem shoppingCartItem : customer.getShoppingCartItems()) {
            shoppingCartItemRepository.delete(shoppingCartItem);
        }
    }
}
