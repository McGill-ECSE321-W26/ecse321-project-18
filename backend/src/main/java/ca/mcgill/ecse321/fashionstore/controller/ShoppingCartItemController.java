package ca.mcgill.ecse321.fashionstore.controller;

import ca.mcgill.ecse321.fashionstore.dto.ShoppingCartItemRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.ShoppingCartItemResponseDto;
import ca.mcgill.ecse321.fashionstore.model.ShoppingCartItem;
import ca.mcgill.ecse321.fashionstore.service.ShoppingCartItemService;
import java.util.List;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Controller for ShoppingCartItem REST API endpoints */
@CrossOrigin
@RestController
@SuppressFBWarnings("SPRING_ENDPOINT")
public class ShoppingCartItemController {
    private ShoppingCartItemService shoppingCartItemService;

    /**
     * Constructor for ShoppingCartItemController.
     *
     * @param shoppingCartItemService ShoppingCartItem service class.
     * @author Cyrus Fung
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ShoppingCartItemController(ShoppingCartItemService shoppingCartItemService) {
        this.shoppingCartItemService = shoppingCartItemService;
    }

    /**
     * Retrieves all the items in a customer's shopping cart.
     *
     * @param customerId Customer ID.
     * @return Returns a list of ShoppingCartItemResponseDto.
     * @author Cyrus Fung
     */
    @GetMapping("/fashionstore/account/customer/{customerId}/shoppingcartitem")
    @ResponseStatus(HttpStatus.OK)
    public List<ShoppingCartItemResponseDto> getShoppingCartItems(@PathVariable int customerId) {
        List<ShoppingCartItem> shoppingCartItems =
                shoppingCartItemService.getShoppingCartItems(customerId);
        return ShoppingCartItemResponseDto.shoppingCartItemResponseDtos(shoppingCartItems);
    }

    /**
     * Creates a new shopping cart item in the customer's shopping cart.
     *
     * @param customerId Customer ID.
     * @param shoppingCartItemRequestDto ShoppingCartItemRequestDto.
     * @return Returns the new ShoppingCartItemResponseDto.
     * @author Cyrus Fung
     */
    @PostMapping("/fashionstore/account/customer/{customerId}/shoppingcartitem")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCartItemResponseDto addShoppingCartItem(
            @PathVariable int customerId,
            @RequestBody ShoppingCartItemRequestDto shoppingCartItemRequestDto) {
        ShoppingCartItem shoppingCartItem =
                shoppingCartItemService.addShoppingCartItem(customerId, shoppingCartItemRequestDto);
        return new ShoppingCartItemResponseDto(shoppingCartItem);
    }

    /**
     * Updates an item in the customer's shopping cart.
     *
     * @param customerId Customer ID.
     * @param shoppingCartItemRequestDto ShoppingCartItemRequestDto.
     * @return Returns the updated ShoppingCartItemResponseDto.
     * @author Cyrus Fung
     */
    @PutMapping("/fashionstore/account/customer/{customerId}/shoppingcartitem/{id}")
    public ShoppingCartItemResponseDto updateShoppingCartItem(
            @PathVariable int customerId,
            @PathVariable int id,
            @RequestBody ShoppingCartItemRequestDto shoppingCartItemRequestDto) {
        ShoppingCartItem shoppingCartItem =
                shoppingCartItemService.updateShoppingCartItem(id, shoppingCartItemRequestDto);
        return new ShoppingCartItemResponseDto(shoppingCartItem);
    }

    /**
     * Delete an item in the customer's shopping cart.
     *
     * @param customerId Customer ID.
     * @param id ShoppingCartItem ID to be deleted.
     * @author Cyrus Fung
     */
    @DeleteMapping("/fashionstore/account/customer/{customerId}/shoppingcartitem/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShoppingCartItem(@PathVariable int customerId, @PathVariable int id) {
        shoppingCartItemService.deleteShoppingCartItem(id);
    }

    /**
     * Delete all items in the customer's shopping cart.
     *
     * @param customerId Customer ID.
     * @author Cyrus Fung
     */
    @DeleteMapping("/fashionstore/account/customer/{customerId}/shoppingcartitem")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShoppingCartItems(@PathVariable int customerId) {
        shoppingCartItemService.deleteShoppingCartItems(customerId);
    }
}
