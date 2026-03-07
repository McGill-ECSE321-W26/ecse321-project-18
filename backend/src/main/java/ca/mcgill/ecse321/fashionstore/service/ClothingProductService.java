package ca.mcgill.ecse321.fashionstore.service;

import static ca.mcgill.ecse321.fashionstore.dto.ClothingProductResponseDto.clothingProductsToResponseDtos;

import ca.mcgill.ecse321.fashionstore.dto.ClothingProductRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.ClothingProductResponseDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * Service method to delete a clothing product and all of its associated clothing items.
     *
     * @param productId ID of the ClothingProduct to delete
     * @throws FashionStoreException if the product ID does not correspond to an existing product
     * @author Kenneth Wang (KennethWang6)
     */
    public void deleteClothingProduct(int productId) {

        ClothingProduct product =
                Utils.findClothingProductById(clothingProductRepository, productId);

        clothingProductRepository.delete(product);
    }

    /**
     * Service method to get all clothing products matching a search by name and/or filters by
     * sizes, colours.
     *
     * @param name Name of clothing product (search)
     * @param sizes Sizes of clothing product (filter)
     * @param colours Colours of clothing product (filter)
     * @return Clothing products matching search and/or filters
     * @author Carolyn Wu (cw118)
     */
    public List<ClothingProductResponseDto> getMatchingClothingProducts(
            String name, List<ClothingItem.Size> sizes, List<ClothingItem.Colour> colours) {
        List<ClothingProduct> clothingProducts = searchClothingProductsByName(name);

        // now try filtering, only if filters were specified
        List<ClothingProduct> matchingClothingProducts =
                filterClothingProductsBySizeColour(clothingProducts, sizes, colours);

        List<ClothingProductResponseDto> clothingProductResponseDtos =
                matchingClothingProducts.isEmpty()
                        ? clothingProductsToResponseDtos(clothingProducts)
                        : clothingProductsToResponseDtos(matchingClothingProducts);

        return clothingProductResponseDtos;
    }

    /**
     * Search ClothingProducts by name. If name is null, the original list is returned.
     *
     * @param name clothing product name to search by.
     * @return list of ClothingProducts whose name contain the search string. If the search string
     *     is null, then this simply returns the full original list.
     * @author Carolyn Wu (cw118)
     */
    private List<ClothingProduct> searchClothingProductsByName(String name) {
        return (name == null)
                ? (List<ClothingProduct>) clothingProductRepository.findAll()
                : clothingProductRepository.findClothingProductsByNameContainsIgnoreCase(name);
    }

    /**
     * Filters ClothingProducts by size and/or colour.
     *
     * @param clothingProducts list of ClothingProducts to filter on.
     * @param sizes size filters.
     * @param colours colour filters.
     * @return list of ClothingProducts that match the given size and/or colour filters.
     * @author Carolyn Wu (cw118)
     */
    private List<ClothingProduct> filterClothingProductsBySizeColour(
            List<ClothingProduct> clothingProducts,
            List<ClothingItem.Size> sizes,
            List<ClothingItem.Colour> colours) {
        List<ClothingProduct> filteredClothingProducts = new ArrayList<>();

        if (!sizes.isEmpty() && !colours.isEmpty()) {
            for (ClothingProduct clothingProduct : clothingProducts) {
                List<ClothingItem> matchingItems =
                        clothingProduct.getItems().stream()
                                .filter(
                                        item ->
                                                sizes.contains(item.getSize())
                                                        && colours.contains(item.getColour()))
                                .toList();

                // if the product has an item matching the size/colour filter, keep/add it!
                if (!matchingItems.isEmpty()) {
                    filteredClothingProducts.add(clothingProduct);
                }
            }
        }

        return filteredClothingProducts;
    }
}
