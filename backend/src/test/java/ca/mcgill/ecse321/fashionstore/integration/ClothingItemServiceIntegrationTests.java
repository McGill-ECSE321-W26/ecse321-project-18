package ca.mcgill.ecse321.fashionstore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.mcgill.ecse321.fashionstore.controller.ClothingProductController;
import ca.mcgill.ecse321.fashionstore.dto.ClothingItemRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.ClothingItemResponseDto;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import org.junit.jupiter.api.AfterEach;
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
    private static final String clothingItemUpdateUri =
            "/fashionstore/clothingproduct/{productId}/clothingitem/{itemId}";

    /** Setup method for the test suite. */
    @BeforeEach
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
    @AfterEach
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

    /**
     * Test successful PUT to update ClothingItem stock.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void updateClothingItemStockOk() {
        int productId = clothingProduct.getId();
        int itemId = clothingItem.getId();

        ClothingItemRequestDto dto =
                new ClothingItemRequestDto(
                        clothingItem.getSize(), clothingItem.getColour(), 50, productId);

        ClothingItemResponseDto response =
                client.put()
                        .uri(clothingItemUpdateUri, productId, itemId)
                        .body(dto)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(ClothingItemResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        assertNotNull(response, "Response body for PUT ClothingItem is null.");
        assertEquals(50, response.numInStock(), "Stock should update to 50.");
    }

    /**
     * Test invalid PUT for ClothingItem (wrong productId).
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void updateClothingItemStockInvalid() {
        int wrongProductId = clothingProduct.getId() + 97;
        int itemId = clothingItem.getId();

        ClothingItemRequestDto dto =
                new ClothingItemRequestDto(
                        clothingItem.getSize(), clothingItem.getColour(), 50, wrongProductId);

        client.put()
                .uri(clothingItemUpdateUri, wrongProductId, itemId)
                .body(dto)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    /**
     * Test successful DELETE for a ClothingItem.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void deleteClothingItemOk() {
        int productId = clothingProduct.getId();
        int itemId = clothingItem.getId();

        client.delete()
                .uri(clothingItemUri, productId, itemId)
                .exchange()
                .expectStatus()
                .isNoContent();

        assertFalse(
                clothingItemRepository.existsById(itemId),
                "ClothingItem should be deleted after DELETE request.");
    }

    /**
     * Test invalid DELETE for a ClothingItem (wrong productId).
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void deleteClothingItemInvalid() {
        int wrongProductId = clothingProduct.getId() + 20;
        int itemId = clothingItem.getId();

        client.delete()
                .uri(clothingItemUri, wrongProductId, itemId)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }
}
