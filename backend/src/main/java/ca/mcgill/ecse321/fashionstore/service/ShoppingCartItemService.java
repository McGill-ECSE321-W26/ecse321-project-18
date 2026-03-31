package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.ShoppingCartItemRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.ShoppingCartItemResponseDto;
import ca.mcgill.ecse321.fashionstore.dto.ShoppingCartListResponseDto;
import ca.mcgill.ecse321.fashionstore.dto.ShoppingCartResponseDto;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.ShoppingCartItem;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.ShoppingCartItemRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
     * @author Cyrus Fung
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
     * @author Cyrus Fung
     */
    public ShoppingCartListResponseDto getShoppingCartItems(int customerId) {
        Customer customer = Utils.findCustomerById(customerRepository, customerId);
        List<ShoppingCartItem> items = customer.getShoppingCartItems();
        List<ShoppingCartItemResponseDto> shoppingCartItemResponseDtos =
                ShoppingCartItemResponseDto.shoppingCartItemResponseDtos(items);
        float newPrice = calculateCartPrice(customer);
        return new ShoppingCartListResponseDto(shoppingCartItemResponseDtos, newPrice);
    }

    /**
     * Service method to add a new shopping cart item to a customer.
     *
     * @param customerId Customer ID.
     * @param shoppingCartItemRequestDto ShoppingCartItem request DTO.
     * @author Cyrus Fung
     */
    @Transactional
    public ShoppingCartResponseDto addShoppingCartItem(
            int customerId, @Valid ShoppingCartItemRequestDto shoppingCartItemRequestDto) {
        ShoppingCartItem newShoppingCartItem = new ShoppingCartItem();
        int clothingItemId = shoppingCartItemRequestDto.clothingItemId();
        ClothingItem clothingItem =
                Utils.findClothingItemById(clothingItemRepository, clothingItemId);
        Customer customer = Utils.findCustomerById(customerRepository, customerId);
        newShoppingCartItem.setClothingItem(clothingItem);
        newShoppingCartItem.setQuantity(shoppingCartItemRequestDto.quantity());
        newShoppingCartItem.setCustomer(customer);
        newShoppingCartItem = shoppingCartItemRepository.save(newShoppingCartItem);
        ShoppingCartItemResponseDto shoppingCartItemResponseDto =
                new ShoppingCartItemResponseDto(newShoppingCartItem);
        float newPrice = calculateCartPrice(customer);
        return new ShoppingCartResponseDto(shoppingCartItemResponseDto, newPrice);
    }

    /**
     * Service method to update the quantity of a shopping cart item.
     *
     * @param id ShoppingCartItem ID to be updated.
     * @param shoppingCartItemRequestDto ShoppingCartItem request DTO.
     * @author Cyrus Fung
     */
    @Transactional
    public ShoppingCartResponseDto updateShoppingCartItem(
            int id, @Valid ShoppingCartItemRequestDto shoppingCartItemRequestDto) {
        ShoppingCartItem shoppingCartItem =
                Utils.findShoppingCartItemById(shoppingCartItemRepository, id);
        shoppingCartItem.setQuantity(shoppingCartItemRequestDto.quantity());
        shoppingCartItem = shoppingCartItemRepository.save(shoppingCartItem);
        ShoppingCartItemResponseDto shoppingCartItemResponseDto =
                new ShoppingCartItemResponseDto(shoppingCartItem);
        Customer customer = shoppingCartItem.getCustomer();
        float newPrice = calculateCartPrice(customer);
        return new ShoppingCartResponseDto(shoppingCartItemResponseDto, newPrice);
    }

    /**
     * Service method to delete a new shopping cart item to a customer.
     *
     * @param id ShoppingCartItem ID to be deleted.
     * @param customerId Customer ID.
     * @author Cyrus Fung
     */
    @Transactional
    public ShoppingCartResponseDto deleteShoppingCartItem(int id, int customerId) {
        Customer customer = Utils.findCustomerById(customerRepository, customerId);
        for (ShoppingCartItem item : customer.getShoppingCartItems()) {
            if (item.getId() == id) {
                customer.removeShoppingCartItem(item);
                break;
            }
        }
        shoppingCartItemRepository.deleteById(id);
        return new ShoppingCartResponseDto(null, calculateCartPrice(customer));
    }

    /**
     * Service method to delete all shopping cart items of a customer.
     *
     * @param customerId Customer ID.
     * @author Cyrus Fung
     */
    @Transactional
    public ShoppingCartResponseDto deleteShoppingCartItems(int customerId) {
        Customer customer = Utils.findCustomerById(customerRepository, customerId);
        for (ShoppingCartItem shoppingCartItem : customer.getShoppingCartItems()) {
            shoppingCartItemRepository.delete(shoppingCartItem);
        }
        return new ShoppingCartResponseDto(null, 0.0f);
    }

    private static float calculateCartPrice(Customer customer) {
        float total = 0.0f;
        for (ShoppingCartItem shoppingCartItem : customer.getShoppingCartItems()) {
            total +=
                    shoppingCartItem.getClothingItem().getClothingProduct().getPrice()
                            * shoppingCartItem.getQuantity();
        }
        BigDecimal bd = new BigDecimal(Float.toString(total)).setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
    }
}
