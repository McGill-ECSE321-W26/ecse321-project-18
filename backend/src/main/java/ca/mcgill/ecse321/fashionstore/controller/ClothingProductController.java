package ca.mcgill.ecse321.fashionstore.controller;

import static ca.mcgill.ecse321.fashionstore.dto.ClothingProductResponseDto.clothingProductsToResponseDtos;

import ca.mcgill.ecse321.fashionstore.dto.ClothingProductRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.ClothingProductResponseDto;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.service.ClothingProductService;
import java.util.List;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Controller for ClothingProduct REST API endpoints */
@RestController
@SuppressFBWarnings("SPRING_ENDPOINT")
public class ClothingProductController {
    private ClothingProductService clothingProductService;

    /**
     * Constructor for ClothingProductController
     *
     * @param clothingProductService clothing product service class
     * @author Jennifer You (jenni4u)
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public ClothingProductController(ClothingProductService clothingProductService) {
        this.clothingProductService = clothingProductService;
    }

    /**
     * Creates a new clothing product
     *
     * @param clothingProductRequestDto the details of the new clothing product to create
     * @return a ClothingProductResponseDto
     * @author Jennifer You (jenni4u)
     */
    @PostMapping("/fashionstore/clothingproduct")
    @ResponseStatus(HttpStatus.CREATED)
    public ClothingProductResponseDto createClothingProduct(
            @RequestBody ClothingProductRequestDto clothingProductRequestDto) {
        return clothingProductService.createClothingProduct(clothingProductRequestDto);
    }

    /**
     * Updates an existing clothing product
     *
     * @param clothingProductRequestDto the details of the clothing product to update
     * @param id the id of the clothing product to update
     * @return a ClothingProductResponseDto
     * @author Jennifer You (jenni4u)
     */
    @PutMapping("/fashionstore/clothingproduct/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClothingProductResponseDto updateClothingProduct(
            @RequestBody ClothingProductRequestDto clothingProductRequestDto,
            @PathVariable int id) {
        return clothingProductService.updateClothingProduct(clothingProductRequestDto, id);
    }

    /**
     * Deletes a clothing product and all of its associated clothing items.
     *
     * @param productId ID of the ClothingProduct to delete
     * @author Kenneth Wang (KennethWang6)
     */
    @DeleteMapping("/fashionstore/clothingproduct/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClothingProduct(@PathVariable int productId) {
        clothingProductService.deleteClothingProduct(productId);
    }

    /**
     * Gets clothing products that match a search by name and/or filters by size, colour.
     *
     * @param name Name of the product (search).
     * @param sizes Sizes of the product (filter).
     * @param colours Colours of the product (filter).
     * @return List of DTOs representing clothing products matching the search and/or filters.
     * @author Carolyn Wu (cw118)
     */
    @GetMapping("/fashionstore/clothingproduct")
    @ResponseBody
    public List<ClothingProductResponseDto> getMatchingClothingProducts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "sizes", required = false) List<ClothingItem.Size> sizes,
            @RequestParam(value = "colours", required = false) List<ClothingItem.Colour> colours) {
        List<ClothingProduct> clothingProducts =
                clothingProductService.getMatchingClothingProducts(name, sizes, colours);

        return clothingProductsToResponseDtos(clothingProducts);
    }

    /**
     * Get a clothing product and all of its clothing items.
     *
     * @param productId ID of the ClothingProduct to get
     * @return A ClothingProductResponseDTO containing details about the product and a list of all
     *     clothing items associated with it.
     * @author Qiuyu Huang (redacted24)
     */
    @GetMapping("/fashionstore/clothingproduct/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ClothingProductResponseDto getClothingProduct(@PathVariable int productId) {
        ClothingProduct clothingProduct = clothingProductService.getClothingProduct(productId);
        return new ClothingProductResponseDto(clothingProduct);
    }
}
