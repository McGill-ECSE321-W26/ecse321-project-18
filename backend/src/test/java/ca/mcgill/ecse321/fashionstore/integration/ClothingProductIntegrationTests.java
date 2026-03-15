package ca.mcgill.ecse321.fashionstore.integration;

import static ca.mcgill.ecse321.fashionstore.dto.ClothingProductResponseDto.clothingProductsToResponseDtos;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ca.mcgill.ecse321.fashionstore.dto.ClothingItemResponseDto;
import ca.mcgill.ecse321.fashionstore.dto.ClothingProductRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.ClothingProductResponseDto;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.util.UriComponentsBuilder;

/** Integration test suite for ClothingProductService. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureRestTestClient
class ClothingProductIntegrationTests {
    @Autowired RestTestClient client;
    @Autowired private ClothingProductRepository clothingProductRepository;
    @Autowired private ClothingItemRepository clothingItemRepository;

    private ClothingProduct clothingProduct;
    private ClothingProduct clothingProduct2;
    private ClothingProductRequestDto clothingProductRequestDto;
    private List<ClothingProduct> allClothingProducts;
    private static final String clothingProductUri = "/fashionstore/clothingproduct/{id}";
    private static final String clothingProductsUri = "/fashionstore/clothingproduct";

    private ClothingItem clothingItem;

    private static final String errorLoc = "$.errors";

    /** Setup method for the test suite. */
    @BeforeEach
    void createClothingProducts() {
        // create clothing products
        this.clothingProduct = createClothingProduct("hoodie.png", 69.99f, "Hoodie");
        this.clothingProduct2 = createClothingProduct("sweater.png", 55.55f, "Sweater");

        // create clothing items
        this.clothingItem =
                createClothingItem(
                        clothingProduct, ClothingItem.Size.M, ClothingItem.Colour.YELLOW, 100);
        createClothingItem(clothingProduct2, ClothingItem.Size.XL, ClothingItem.Colour.RED, 50);

        this.allClothingProducts = List.of(clothingProduct, clothingProduct2);

        this.clothingProductRequestDto =
                new ClothingProductRequestDto("T-Shirt", 29.99f, "tshirt.png");
    }

    private ClothingProduct createClothingProduct(String image, float price, String name) {
        ClothingProduct product = new ClothingProduct();
        product.setImage(image);
        product.setPrice(price);
        product.setName(name);
        return clothingProductRepository.save(product);
    }

    private ClothingItem createClothingItem(
            ClothingProduct product,
            ClothingItem.Size size,
            ClothingItem.Colour colour,
            int stock) {
        ClothingItem item = new ClothingItem();
        item.setClothingProduct(product);
        item.setSize(size);
        item.setColour(colour);
        item.setNumInStock(stock);
        return clothingItemRepository.save(item);
    }

    /** Teardown method for test suite. (placeholder, please modify if needed) */
    @AfterEach
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
        int productId = clothingProduct.getId() - 1; // nonexistent clothingproduct id
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
        List<ClothingProductResponseDto> expected =
                clothingProductsToResponseDtos(this.allClothingProducts);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(clothingProductsUri);
        List<ClothingProductResponseDto> response =
                getMatchingClothingProductsResponseBody(builder);

        assertNotNull(response, "Response body for get all clothing products is null.");
        assertIterableEquals(
                expected, response, "Not all clothing products were retrieved correctly.");
    }

    /**
     * Test finding no matching clothing products for given search.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void testSearchNoMatchingClothingProducts() {
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromUriString(clothingProductsUri)
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
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromUriString(clothingProductsUri)
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
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromUriString(clothingProductsUri)
                        .queryParam(
                                "colours",
                                List.of(ClothingItem.Colour.BLUE, ClothingItem.Colour.PURPLE));

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
        List<ClothingProductResponseDto> expected =
                List.of(new ClothingProductResponseDto(clothingProduct));

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromUriString(clothingProductsUri)
                        .queryParam("name", "hood")
                        .queryParam("sizes", List.of(ClothingItem.Size.M))
                        .queryParam(
                                "colours",
                                List.of(ClothingItem.Colour.YELLOW, ClothingItem.Colour.RED));

        List<ClothingProductResponseDto> response =
                getMatchingClothingProductsResponseBody(builder);

        assertNotNull(response, "Response body for get matching clothing products is null.");
        assertIterableEquals(expected, response, "Matching clothing products are incorrect.");
    }

    private List<ClothingProductResponseDto> getMatchingClothingProductsResponseBody(
            UriComponentsBuilder builder) {
        return client.get()
                .uri(builder.build().encode().toUri())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<List<ClothingProductResponseDto>>() {})
                .returnResult()
                .getResponseBody();
    }

    private void assertNoMatchingClothingProducts(List<ClothingProductResponseDto> response) {
        assertNotNull(
                response, "Response body for no matching clothing products should not be null.");
        assertTrue(response.isEmpty(), "List of matching clothing products is not empty.");
    }

    private void assertBodyResponse(
            ClothingProductRequestDto body, ClothingProductResponseDto response) {
        assertNotNull(response, "Response body for creating clothing product should not be null.");
        assertNotNull(
                clothingProductRepository.findClothingProductById(response.id()),
                "Clothing product is not saved in the repository.");
        assertEquals(
                body.name(), response.name(), "Clothing product name is different than expected.");
        assertEquals(
                body.price(),
                response.price(),
                "Clothing product price is different than expected.");
        assertEquals(
                body.image(),
                response.image(),
                "Clothing product image is different than expected.");
    }

    /**
     * Test creating a new clothing product.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void createClothingProductSuccess() {
        ClothingProductResponseDto response =
                client.post()
                        .uri("/fashionstore/clothingproduct")
                        .body(this.clothingProductRequestDto)
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .expectBody(ClothingProductResponseDto.class)
                        .returnResult()
                        .getResponseBody();
        assertBodyResponse(this.clothingProductRequestDto, response);
    }

    /**
     * Test updating a clothing product with valid id.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void updateClothingProductValidId() {
        ClothingProductResponseDto response =
                client.put()
                        .uri(clothingProductUri, this.clothingProduct.getId())
                        .body(this.clothingProductRequestDto)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(ClothingProductResponseDto.class)
                        .returnResult()
                        .getResponseBody();
        assertBodyResponse(this.clothingProductRequestDto, response);
    }

    /**
     * Test updating a clothing product with invalid id.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void updateClothingProductInvalidId() {
        int invalidId = this.clothingProduct.getId() - 1;

        client.put()
                .uri(clothingProductUri, invalidId)
                .body(this.clothingProductRequestDto)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath(errorLoc)
                .isEqualTo(String.format("ClothingProduct ID %d was not found.", invalidId));
    }
}
