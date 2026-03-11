package ca.mcgill.ecse321.fashionstore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.mcgill.ecse321.fashionstore.controller.ClothingProductController;
import ca.mcgill.ecse321.fashionstore.dto.ClothingItemResponseDto;
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

/** Integration test suite for ClothingItemService. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureRestTestClient
class ClothingItemServiceIntegrationTests {
    @Autowired RestTestClient client;
    @Autowired ClothingProductController clothingProductController;
    @Autowired private ClothingProductRepository clothingProductRepository;
    @Autowired private ClothingItemRepository clothingItemRepository;

    private ClothingProduct clothingProduct;
    private ClothingItem clothingItem;
    private static final String clothingProductUri = "/fashionstore/clothingproduct/{id}";
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
                        .uri(clothingItemUri, productId, itemId)
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
}
