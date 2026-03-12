package ca.mcgill.ecse321.fashionstore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.fashionstore.controller.ClothingProductController;
import ca.mcgill.ecse321.fashionstore.dto.ClothingItemResponseDto;
import ca.mcgill.ecse321.fashionstore.dto.ClothingProductResponseDto;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/** Integration test suite for ClothingProductService. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureRestTestClient
class ClothingProductServiceIntegrationTests {
    @Autowired RestTestClient client;
    @Autowired ClothingProductController clothingProductController;
    @Autowired private ClothingProductRepository clothingProductRepository;
    @Autowired private ClothingItemRepository clothingItemRepository;

    private ClothingProduct clothingProduct;
    private static final String clothingProductUri = "/fashionstore/clothingproduct/{id}";

    private ClothingItem clothingItem;
    private static final String clothingItemUri =
            "/fashionstore/clothingproduct/{productId}/clothingitem/{itemId}";

    /** Setup method for the test suite. */
    @BeforeAll
    void createClothingProducts() {
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
     * Test retrieving a specific clothing product and its items.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void getClothingProductOk() {
        int productId = clothingProduct.getId();
        ClothingProductResponseDto response =
                client.get()
                        .uri(clothingProductUri, productId)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(ClothingProductResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(response, "Response body for get clothing product is null.");
        assertNotNull(
                response.clothingItems(),
                "ClothingProduct should have a nonempty list of ClothingItems.");
        assertEquals(
                response.clothingItems().getFirst(),
                new ClothingItemResponseDto(clothingItem),
                "ClothingProduct items are different than expected.");
    }

    /**
     * Test retrieving a nonexistent clothing product fails.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void getClothingProductInvalid() {
        int productId = clothingProduct.getId() + 1; // nonexistent clothingproduct id
        ClothingProductResponseDto response =
                client.get()
                        .uri(clothingProductUri, productId)
                        .exchange()
                        .expectStatus()
                        .isNotFound()
                        .expectBody(ClothingProductResponseDto.class)
                        .returnResult()
                        .getResponseBody();
        assertNotNull(
                response, "Response should not be null for a failed GET for clothing product.");
        assertNull(
                response.name(),
                "Response name should be null for a failed GET for clothing product.");
    }

    /**
     * Test successful DELETE for a ClothingProduct.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void deleteClothingProductOk() {
        int productId = clothingProduct.getId();

        client.delete().uri(clothingProductUri, productId).exchange().expectStatus().isNoContent();

        assertFalse(
                clothingProductRepository.existsById(productId),
                "ClothingProduct should be deleted after DELETE request.");

        assertFalse(
                clothingItemRepository.existsById(clothingItem.getId()),
                "Associated ClothingItems should also be deleted.");
    }

    /**
     * Test invalid DELETE for a ClothingProduct (nonexistent ID).
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void deleteClothingProductInvalid() {
        int badId = clothingProduct.getId() + 999;

        client.delete().uri(clothingProductUri, badId).exchange().expectStatus().isNotFound();
    }
}
