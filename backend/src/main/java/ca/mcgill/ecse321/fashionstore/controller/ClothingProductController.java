package ca.mcgill.ecse321.fashionstore.controller;

import ca.mcgill.ecse321.fashionstore.dto.ClothingProductRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.ClothingProductResponseDto;
import ca.mcgill.ecse321.fashionstore.service.ClothingProductService;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
}
