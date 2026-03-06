package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.ClothingProductRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.ClothingProductResponseDto;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import jakarta.validation.Valid;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/** Service class for ClothingProduct. */
@Service
@Validated
public class ClothingProductService {
    private ClothingProductRepository clothingProductRepository;

    /**
     * Constructor for ClothingProductService class
     *
     * @param clothingProductRepository clothing product repository class
     * @author Jennifer You (jenni4u)
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ClothingProductService(ClothingProductRepository clothingProductRepository) {
        this.clothingProductRepository = clothingProductRepository;
    }

    /**
     * Service method to create a new clothing product
     *
     * @param clothingProductRequestDto Clothing Product Request DTO
     * @return Clothing Product Response DTO
     * @author Jennifer You (jenni4u)
     */
    public ClothingProductResponseDto createClothingProduct(
            @Valid ClothingProductRequestDto clothingProductRequestDto) {
        // create new clothing product
        ClothingProduct clothingProduct = new ClothingProduct();
        clothingProduct.setName(clothingProductRequestDto.name());
        clothingProduct.setPrice(clothingProductRequestDto.price());
        clothingProduct.setImage(clothingProductRequestDto.image());

        // save clothing product to repository
        clothingProduct = this.clothingProductRepository.save(clothingProduct);
        return new ClothingProductResponseDto(clothingProduct);
    }

    /**
     * Service method to update an existing clothing product
     *
     * @param clothingProductRequestDto Clothing Product Request DTO
     * @param clothingProductId ID of the clothing product to update
     * @return Clothing Product Response DTO
     * @author Jennifer You (jenni4u)
     */
    public ClothingProductResponseDto updateClothingProduct(
            @Valid ClothingProductRequestDto clothingProductRequestDto, int clothingProductId) {
        // find clothing product to update
        ClothingProduct clothingProduct =
                Utils.findClothingProductById(clothingProductRepository, clothingProductId);

        // update clothing product details
        clothingProduct.setName(clothingProductRequestDto.name());
        clothingProduct.setPrice(clothingProductRequestDto.price());
        clothingProduct.setImage(clothingProductRequestDto.image());

        // save updated clothing product to repository
        clothingProduct = this.clothingProductRepository.save(clothingProduct);
        return new ClothingProductResponseDto(clothingProduct);
    }
}
