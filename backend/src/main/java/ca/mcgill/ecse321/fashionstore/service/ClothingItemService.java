package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.ClothingItemRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.ClothingItemResponseDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import jakarta.validation.Valid;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/** Service class for ClothingItem. */
@Service
@Validated
public class ClothingItemService {
    private ClothingItemRepository clothingItemRepository;
    private ClothingProductRepository clothingProductRepository;

    /**
     * Constructor for ClothingItemService class
     *
     * @param clothingItemRepository clothing item repository class
     * @author Jennifer You (jenni4u)
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ClothingItemService(
            ClothingItemRepository clothingItemRepository,
            ClothingProductRepository clothingProductRepository) {
        this.clothingItemRepository = clothingItemRepository;
        this.clothingProductRepository = clothingProductRepository;
    }

    /**
     * Service method to create a new clothing item
     *
     * @param clothingItemRequestDto Clothing Product Request DTO
     * @return Clothing Product Response DTO
     * @author Jennifer You (jenni4u)
     */
    public ClothingItemResponseDto createClothingItem(
            @Valid ClothingItemRequestDto clothingItemRequestDto) {
        // create new clothing item
        ClothingItem clothingItem = new ClothingItem();
        clothingItem.setSize(clothingItemRequestDto.size());
        clothingItem.setColour(clothingItemRequestDto.colour());
        clothingItem.setNumInStock(clothingItemRequestDto.numInStock());
        ClothingProduct clothingProduct =
                Utils.findClothingProductById(
                        this.clothingProductRepository, clothingItemRequestDto.clothingProductId());
        clothingItem.setClothingProduct(clothingProduct);

        // save clothing item to repository
        clothingItem = this.clothingItemRepository.save(clothingItem);
        return new ClothingItemResponseDto(clothingItem);
    }

    /**
     * Service method to update the stock quantity of an existing clothing item.
     *
     * @param productId ID of the ClothingProduct the item belongs to
     * @param itemId ID of the ClothingItem to update
     * @param clothingItemRequestDto ClothingItem Request DTO containing the new stock quantity
     * @return clothingItemResponseDto updated ClothingItem Response DTO
     * @author Kenneth Wang (KennethWang6)
     */
    public ClothingItemResponseDto updateClothingItemStock(
            int productId, int itemId, @Valid ClothingItemRequestDto clothingItemRequestDto) {

        ClothingProduct product =
                Utils.findClothingProductById(clothingProductRepository, productId);

        ClothingItem item = Utils.findClothingItemById(clothingItemRepository, itemId);

        if (item.getClothingProduct() == null
                || item.getClothingProduct().getId() != product.getId()) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST,
                    String.format(
                            "ClothingItem ID %d does not belong to ClothingProduct ID %d.",
                            itemId, productId));
        }

        // update stock
        item.setNumInStock(clothingItemRequestDto.numInStock());

        item = clothingItemRepository.save(item);

        return new ClothingItemResponseDto(item);
    }

    /**
     * Service method to delete a clothing item from a clothing product.
     *
     * @param productId ID of the ClothingProduct the item belongs to
     * @param itemId ID of the ClothingItem to delete
     * @throws FashionStoreException if the item does not belong to the specified product
     * @author Kenneth Wang (KennethWang6)
     */
    public void deleteClothingItem(int productId, int itemId) {

        ClothingProduct product =
                Utils.findClothingProductById(clothingProductRepository, productId);

        ClothingItem item = Utils.findClothingItemById(clothingItemRepository, itemId);

        if (item.getClothingProduct() == null
                || item.getClothingProduct().getId() != product.getId()) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST,
                    String.format(
                            "ClothingItem ID %d does not belong to ClothingProduct ID %d.",
                            itemId, productId));
        }

        clothingItemRepository.delete(item);
    }

    /**
     * Service method to retrieve a clothing item and all of its details
     *
     * @param productId ID of the ClothingProduct that the ClothingItem belongs to
     * @param itemId ID of the ClothingItem to retrieve
     * @return A ClothingItemResponseDTO containing details about the clothing item.
     * @throws FashionStoreException if the item ID does not correspond to an existing clothing
     *     item.
     * @author Qiuyu Huang (redacted24)
     */
    public ClothingItemResponseDto getClothingItem(int productId, int itemId) {
        // Find ClothingItem by ID
        ClothingItem clothingItem = Utils.findClothingItemById(clothingItemRepository, itemId);
        // Find ClothingProduct by ID
        ClothingProduct clothingProduct =
                Utils.findClothingProductById(clothingProductRepository, productId);
        // Verify that clothingItem is valid and belongs to clothingProduct
        if (clothingItem.getClothingProduct() != null
                && clothingItem.getClothingProduct().getId() == clothingProduct.getId()) {
            return new ClothingItemResponseDto(clothingItem);
        } else {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST,
                    String.format(
                            "ClothingItem with ID: %d does not belong to ClothingProduct with id: %d",
                            clothingItem.getId(), clothingProduct.getId()));
        }
    }
}
