package ca.mcgill.ecse321.fashionstore.controller;

import ca.mcgill.ecse321.fashionstore.dto.ClothingItemRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.ClothingItemResponseDto;
import ca.mcgill.ecse321.fashionstore.service.ClothingItemService;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Controller for ClothingItem REST API endpoints */
@RestController
@SuppressFBWarnings("SPRING_ENDPOINT")
public class ClothingItemController {
    private ClothingItemService clothingItemService;

    /**
     * Constructor for ClothingItemController class
     *
     * @param clothingItemService clothing item service class
     * @author Jennifer You (jenni4u)
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ClothingItemController(ClothingItemService clothingItemService) {
        this.clothingItemService = clothingItemService;
    }

    /**
     * REST API endpoint to create a new clothing item
     *
     * @param clothingItemRequestDto Clothing Item Request DTO
     * @return Clothing Item Response DTO
     * @author Jennifer You (jenni4u)
     */
    @PostMapping("/fashionstore/clothingproduct/{id}/clothingitem")
    @ResponseStatus(HttpStatus.CREATED)
    public ClothingItemResponseDto createClothingItem(
            @RequestBody ClothingItemRequestDto clothingItemRequestDto) {
        return this.clothingItemService.createClothingItem(clothingItemRequestDto);
    }

    /**
     * Updates the stock quantity of a clothing item.
     *
     * @param productId ID of the ClothingProduct the item belongs to
     * @param itemId ID of the ClothingItem to update
     * @param clothingItemRequestDto ClothingItem Request DTO containing the new stock quantity
     * @return ClothingItemResponseDto updated ClothingItem Response DTO
     * @author Kenneth Wang (KennethWang6)
     */
    @PutMapping("/fashionstore/clothingproduct/{productId}/clothingitem/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ClothingItemResponseDto updateClothingItemStock(
            @PathVariable int productId,
            @PathVariable int itemId,
            @RequestBody ClothingItemRequestDto clothingItemRequestDto) {

        return clothingItemService.updateClothingItemStock(
                productId, itemId, clothingItemRequestDto);
    }

    /**
     * Deletes a clothing item from a clothing product.
     *
     * @param productId ID of the ClothingProduct the item belongs to
     * @param itemId ID of the ClothingItem to delete
     * @author Kenneth Wang (KennethWang6)
     */
    @DeleteMapping("/fashionstore/clothingproduct/{productId}/clothingitem/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClothingItem(@PathVariable int productId, @PathVariable int itemId) {

        clothingItemService.deleteClothingItem(productId, itemId);
    }

    /**
     * Get a specific clothing item to display its details.
     *
     * @param itemId ID of the ClothingProduct to get
     * @author Qiuyu Huang (redacted24)
     */
    @GetMapping("/fashionstore/clothingproduct/{id}")
    public void getClothingProduct(@PathVariable int itemId) {}
}
