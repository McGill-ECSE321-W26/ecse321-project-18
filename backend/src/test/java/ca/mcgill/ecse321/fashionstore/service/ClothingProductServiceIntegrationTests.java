package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import ca.mcgill.ecse321.fashionstore.controller.ClothingProductController;
import ca.mcgill.ecse321.fashionstore.dto.ClothingProductRequestDto;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/** Integration test suite for ClothingProductService. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClothingProductServiceIntegrationTests {
    @Autowired ClothingProductController clothingProductController;
    @Autowired private ClothingProductRepository clothingProductRepository;
    @Autowired private ClothingItemRepository clothingItemRepository;

    /** Teardown method for test suite. (placeholder, please modify if needed) */
    @AfterAll
    void clearDatabase() {
        clothingItemRepository.deleteAll();
        clothingProductRepository.deleteAll();
    }

    /** Test that a clothing item exists. (placeholder, please modify if needed) */
    @Test
    void test1() {
        ClothingProductRequestDto request =
                new ClothingProductRequestDto("hoodie", 69.99f, "hoodie.png");
        assertDoesNotThrow(
                () -> clothingProductController.createClothingProduct(request), "should not throw");
    }
}
