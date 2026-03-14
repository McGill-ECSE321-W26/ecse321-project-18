package ca.mcgill.ecse321.fashionstore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.mcgill.ecse321.fashionstore.controller.ClothingProductController;
import ca.mcgill.ecse321.fashionstore.dto.ClothingItemRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.ClothingItemResponseDto;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/** Integration test suite for ClothingItemService. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureRestTestClient
class ClothingItemIntegrationTests {
    @Autowired RestTestClient client;
    @Autowired ClothingProductController clothingProductController;
    @Autowired private ClothingProductRepository clothingProductRepository;
    @Autowired private ClothingItemRepository clothingItemRepository;

    private ClothingProduct clothingProduct;
    private ClothingItem clothingItem;
    private ClothingItemRequestDto validClothingItemRequestDto;
    private ClothingItemRequestDto invalidClothingItemRequestDto;
    private static final String clothingProductUri = "/fashionstore/clothingproduct/{id}";
    private static final String clothingItemUri = "/fashionstore/clothingproduct/{id}/clothingitem";
    private static final String clothingItemsUri =
            "/fashionstore/clothingproduct/{productId}/clothingitem/{itemId}";
    private static final String errorLoc = "$.errors";

    /** Setup method for the test suite. */
    @BeforeEach
    void createClothingProducts() {
        createClothingItems();
        // create DTOs
        this.validClothingItemRequestDto =
                new ClothingItemRequestDto(
                        ClothingItem.Size.S,
                        ClothingItem.Colour.BLACK,
                        50,
                        this.clothingProduct.getId());
        this.invalidClothingItemRequestDto =
                new ClothingItemRequestDto(
                        ClothingItem.Size.S,
                        ClothingItem.Colour.BLACK,
                        50,
                        this.clothingProduct.getId() + 1);
    }

    private void createClothingItems() {
        // set clothing product first
        ClothingProduct clothingProduct = new ClothingProduct();
        clothingProduct.setImage("hoodie.png");
        clothingProduct.setPrice(69.99f);
        clothingProduct.setName("Hoodie");
        clothingProductRepository.save(clothingProduct);
        this.clothingProduct = clothingProduct;

        // create first clothing item
        ClothingItem clothingItem = new ClothingItem();
        clothingItem.setClothingProduct(clothingProduct);
        clothingItem.setSize(ClothingItem.Size.M);
        clothingItem.setColour(ClothingItem.Colour.YELLOW);
        clothingItem.setNumInStock(100);
        clothingItemRepository.save(clothingItem);
        this.clothingItem = clothingItem;
    }

    /** Teardown method for test suite. (placeholder, please modify if needed) */
    @AfterAll
    void clearDatabase() {
        clothingItemRepository.deleteAll();
        clothingProductRepository.deleteAll();
    }

    /**
     * Test successful GET for a ClothingItem.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void getClothingItemOk() {
        int productId = clothingProduct.getId();
        int itemId = clothingItem.getId();
        ClothingItemResponseDto response =
                client.get()
                        .uri(clothingItemsUri, productId, itemId)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(ClothingItemResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(response, "Response body for GET ClothingItem is null.");
        assertEquals(
                response.size(),
                clothingItem.getSize(),
                "Wrong size for response of GET ClothingItem.");
        assertEquals(
                response.id(), clothingItem.getId(), "Wrong Id for response of GET ClothingItem.");
    }

    private void assertBodyResponse(ClothingItemRequestDto body, ClothingItemResponseDto response) {
        assertNotNull(response, "Response body for creating clothing item should not be null.");
        assertNotNull(
                this.clothingItemRepository.findClothingItemById(response.id()),
                "Clothing product is not saved in the repository.");
        assertEquals(
                body.size(), response.size(), "Clothing item size is different than expected.");
        assertEquals(
                body.numInStock(),
                response.numInStock(),
                "Clothing item stock is different than expected.");
        assertEquals(
                body.colour(),
                response.colour(),
                "Clothing item colour is different than expected.");
        assertEquals(
                body.clothingProductId(),
                response.clothingProductId(),
                "Clothing item's associated product is different than expected.");
    }

    /**
     * Test creating a new clothing item with a valid request.
     *
     * @author Jennife You (jenni4u)
     */
    @Test
    void createClothingItemValidRequest() {
        ClothingItemResponseDto response =
                client.post()
                        .uri(clothingItemUri, this.clothingProduct.getId())
                        .body(this.validClothingItemRequestDto)
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .expectBody(ClothingItemResponseDto.class)
                        .returnResult()
                        .getResponseBody();
        assertBodyResponse(this.validClothingItemRequestDto, response);
    }

    /**
     * Test creating a new clothing item with an invalid Product ID fails.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void createClothingItemInvalidId() {
        Integer invalidId = this.clothingProduct.getId() + 1;
        client.post()
                .uri(clothingItemUri, invalidId)
                .body(this.validClothingItemRequestDto)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath(errorLoc)
                .isEqualTo(
                        String.format(
                                "Path variable productId %d does not match clothingProductId in request body %d.",
                                invalidId, validClothingItemRequestDto.clothingProductId()));
    }

    /**
     * Test creating a new clothign item with an invalid request.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void createClothingItemInvalidRequest() {
        client.post()
                .uri(clothingItemUri, this.clothingProduct.getId())
                .body(this.invalidClothingItemRequestDto)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath(errorLoc)
                .isEqualTo(
                        String.format(
                                "Path variable productId %d does not match clothingProductId in request body %d.",
                                this.clothingProduct.getId(),
                                invalidClothingItemRequestDto.clothingProductId()));
    }

    /**
     * Test creating new clothing item with inexistent product id fails.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void updateClothingItemValidRequestId() {
        client.post()
                .uri(clothingItemUri, this.invalidClothingItemRequestDto.clothingProductId())
                .body(this.invalidClothingItemRequestDto)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath(errorLoc)
                .isEqualTo(
                        String.format(
                                "ClothingProduct ID %d was not found.",
                                this.invalidClothingItemRequestDto.clothingProductId()));
    }
}
