package ca.mcgill.ecse321.fashionstore.integration;

import static ca.mcgill.ecse321.fashionstore.dto.ClothingProductResponseDto.clothingProductsToResponseDtos;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

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
    private ClothingProduct clothingProduct2;
    private List<ClothingProduct> allClothingProducts;
    private static final String clothingProductUri = "/fashionstore/clothingproduct/{id}";
    private static final String clothingProductsUri = "/fashionstore/clothingproduct";

    private ClothingItem clothingItem;
    private ClothingItem clothingItem2;
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

        // create another clothing product
        ClothingProduct clothingProduct2 = new ClothingProduct();
        clothingProduct2.setImage("sweater.png");
        clothingProduct2.setPrice(55.55f);
        clothingProduct2.setName("Sweater");
        clothingProductRepository.save(clothingProduct2);
        this.clothingProduct2 = clothingProduct2;

        // another clothing item
        ClothingItem clothingItem2 = new ClothingItem();
        clothingItem2.setClothingProduct(clothingProduct2);
        clothingItem2.setSize(ClothingItem.Size.XL);
        clothingItem2.setColour(ClothingItem.Colour.RED);
        clothingItem2.setNumInStock(50);
        clothingItemRepository.save(clothingItem2);
        this.clothingItem2 = clothingItem2;

        this.allClothingProducts = List.of(clothingProduct, clothingProduct2);
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
     * Test retrieving all clothing products by providing no search and no filters.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void testGetAllClothingProducts() {
        List<ClothingProductResponseDto> expected = clothingProductsToResponseDtos(this.allClothingProducts);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(clothingProductsUri);
        List<ClothingProductResponseDto> response =
            getMatchingClothingProductsResponseBody(builder);

        assertNotNull(response, "Response body for get all clothing products is null.");
        assertIterableEquals(expected, response, "Not all clothing products were retrieved correctly.");
    }

    /**
     * Test finding no matching clothing products for given search.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void testSearchNoMatchingClothingProducts() {
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(clothingProductsUri)
            .queryParam("name", "strawberry");

        List<ClothingProductResponseDto> response =
            getMatchingClothingProductsResponseBody(builder);

        assertNoMatchingClothingProducts(response);
    }

    /**
     * Test finding no matching clothing products for given size filters.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void testFilterSizeNoMatchingClothingProducts() {
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(clothingProductsUri)
            .queryParam("sizes", List.of(ClothingItem.Size.S, ClothingItem.Size.L));

        List<ClothingProductResponseDto> response =
            getMatchingClothingProductsResponseBody(builder);

        assertNoMatchingClothingProducts(response);
    }

    /**
     * Test finding no matching clothing products for given colour filters.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void testFilterColourNoMatchingClothingProducts() {
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(clothingProductsUri)
            .queryParam("colours", List.of(ClothingItem.Colour.BLUE, ClothingItem.Colour.PURPLE));

        List<ClothingProductResponseDto> response =
            getMatchingClothingProductsResponseBody(builder);

        assertNoMatchingClothingProducts(response);
    }

    /**
     * Test retrieving clothing product(s) that match(es) given search and filters.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void testMatchingClothingProducts() {
        List<ClothingProductResponseDto> expected = List.of(new ClothingProductResponseDto(clothingProduct));

        UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(clothingProductsUri)
            .queryParam("name", "hood")
            .queryParam("sizes", List.of(ClothingItem.Size.M))
            .queryParam("colours", List.of(ClothingItem.Colour.YELLOW, ClothingItem.Colour.RED));

        List<ClothingProductResponseDto> response =
            getMatchingClothingProductsResponseBody(builder);

        assertNotNull(response, "Response body for get matching clothing products is null.");
        assertIterableEquals(expected, response, "Matching clothing products are incorrect.");
    }

    private List<ClothingProductResponseDto> getMatchingClothingProductsResponseBody(UriComponentsBuilder builder) {
        return
            client.get()
                .uri(builder.build().encode().toUri())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<List<ClothingProductResponseDto>>() {})
                .returnResult()
                .getResponseBody();
    }

    private void assertNoMatchingClothingProducts(List<ClothingProductResponseDto> response) {
        assertNotNull(response, "Response body for no matching clothing products should not be null.");
        assertTrue(response.isEmpty(), "List of matching clothing products is not empty.");
    }
}
