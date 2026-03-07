package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.ClothingItemRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.ClothingItemResponseDto;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import jakarta.validation.Valid;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
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
}
