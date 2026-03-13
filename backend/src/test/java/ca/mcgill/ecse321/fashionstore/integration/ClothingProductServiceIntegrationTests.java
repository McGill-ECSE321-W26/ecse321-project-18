package ca.mcgill.ecse321.fashionstore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.fashionstore.controller.ClothingProductController;
import ca.mcgill.ecse321.fashionstore.dto.ClothingItemResponseDto;
import ca.mcgill.ecse321.fashionstore.dto.ClothingProductRequestDto;
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
    void getClothingProductFail() {
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
     * Test creating a new clothing product with valid input saves successfully into the repository.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void createClothingProductSave() {
        ClothingProductResponseDto response =
                client.post()
                        .uri("/fashionstore/clothingproduct")
                        .body(new ClothingProductRequestDto("T-Shirt", 29.99f, "tshirt.png"))
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .expectBody(ClothingProductResponseDto.class)
                        .returnResult()
                        .getResponseBody();
        assertNotNull(response, "Response body for creating clothing product should not be null.");
        assertNotNull(
                clothingProductRepository.findClothingProductById(response.id()),
                "Clothing product is not saved in the repository.");
    }

    /**
     * Test creating a new clothing product matches given input
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void createClothingProductAttributes() {
        ClothingProductResponseDto response =
                client.post()
                        .uri("/fashionstore/clothingproduct")
                        .body(new ClothingProductRequestDto("T-Shirt", 29.99f, "tshirt.png"))
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .expectBody(ClothingProductResponseDto.class)
                        .returnResult()
                        .getResponseBody();
        assertEquals(
                "T-Shirt", response.name(), "Clothing product name is different than expected.");
        assertEquals(
                29.99f, response.price(), "Clothing product price is different than expected.");
        assertEquals(
                "tshirt.png",
                response.image(),
                "Clothing product image is different than expected.");
    }

    /**
     * Test updating a clothing product.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void updateClothingProduct() {
        ClothingProductResponseDto response =
                client.put()
                        .uri(clothingProductUri, clothingProduct.getId())
                        .body(new ClothingProductRequestDto("Crewneck", 49.99f, "crewneck.png"))
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(ClothingProductResponseDto.class)
                        .returnResult()
                        .getResponseBody();
        assertEquals(
                "Crewneck", response.name(), "Clothing product name is different than expected.");
        assertEquals(
                49.99f, response.price(), "Clothing product price is different than expected.");
        assertEquals(
                "crewneck.png",
                response.image(),
                "Clothing product image is different than expected.");
    }
}
