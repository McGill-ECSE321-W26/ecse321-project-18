package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.fashionstore.dto.ClothingProductRequestDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

/** Test suite for clothing product service class. */
@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class ClothingProductServiceTests {
    @Mock private ClothingProductRepository clothingProductRepository;
    @Mock private ClothingItemRepository clothingItemRepository;

    @InjectMocks private ClothingProductService clothingProductService;

    private ClothingProduct clothingProduct;
    private ClothingItem clothingItem;
    private ClothingProductRequestDto clothingProductRequestDto;

    /**
     * Creates and saves a clothing product, and a clothing item.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @BeforeEach
    void createClothingItems() {
        // set clothing product first
        ClothingProduct clothingProduct = new ClothingProduct();
        clothingProduct.setImage("hoodie.png");
        clothingProduct.setPrice(69.99f);
        clothingProduct.setName("Hoodie");
        this.clothingProduct = clothingProduct;

        // create first clothing item
        ClothingItem clothingItem = new ClothingItem();
        clothingItem.setClothingProduct(clothingProduct);
        clothingItem.setSize(ClothingItem.Size.M);
        clothingItem.setColour(ClothingItem.Colour.YELLOW);
        clothingItem.setNumInStock(100);
        this.clothingItem = clothingItem;

        this.clothingProductRequestDto =
                new ClothingProductRequestDto("T-Shirt", 29.99f, "tshirt.png");
    }

    /**
     * Deletes all clothing items from database.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @AfterEach
    void clearDatabase() {
        clothingItemRepository.deleteAll();
        clothingProductRepository.deleteAll();
    }

    /**
     * Test retrieving a non-existing clothingProduct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void getNonExistingClothingProduct() {
        // Arrange
        int id = clothingProduct.getId() + 1;
        when(clothingProductRepository.findById(id)).thenReturn(Optional.empty());
        // Act and assert
        assertThrows(
                FashionStoreException.class,
                () -> clothingProductService.getClothingProduct(id),
                "Non existing ClothingProduct search should throw an exception.");
    }

    /**
     * Helper assert function.
     *
     * @author Qiuyu Huang (redacted24)
     */
    private void assertClothingProduct(ClothingProduct expected, ClothingProduct actual) {
        assertEquals(
                expected.getImage(),
                actual.getImage(),
                "Image path of retrieved ClothingProduct does not match.");
        assertEquals(
                expected.getName(),
                actual.getName(),
                "Name of retrieved ClothingProduct does not match.");
        assertEquals(
                expected.getPrice(),
                actual.getPrice(),
                "Price of retrieved ClothingProduct does not match.");
    }

    /**
     * Test retrieving an existing clothingProduct. Does not check for clothing items.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void getClothingProduct() {
        // Arrange
        int id = clothingProduct.getId();
        when(clothingProductRepository.findById(id)).thenReturn(Optional.of(clothingProduct));

        // Act
        ClothingProduct response = clothingProductService.getClothingProduct(id);

        // Assert
        assertClothingProduct(clothingProduct, response);
        verify(clothingProductRepository, times(1)).findById(id);
    }

    /**
     * Test retrieving an existing clothingProduct and check for its clothing items. It is assumed
     * that the GET works and returns a valid ClothingProduct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void getClothingProductCheckItems() {
        // Arrange
        when(clothingProductRepository.findById(clothingProduct.getId()))
                .thenReturn(Optional.of(clothingProduct));

        // Act
        ClothingProduct response =
                clothingProductService.getClothingProduct(clothingProduct.getId());

        // Assert
        assertEquals(
                1,
                response.getItems().size(),
                String.format(
                        "ClothingProduct item list size is %d, should be %d.",
                        response.getItems().size(), clothingProduct.getItems().size()));
        assertEquals(
                clothingItem.getColour(),
                response.getItem(0).getColour(),
                "Retrieved clothing product's item does not have correct colour.");
        verify(clothingProductRepository, times(1)).findById(clothingProduct.getId());
        verify(clothingProductRepository, times(1)).findById(clothingProduct.getId());
    }

    /**
     * Test running search by name on clothing products, expecting no products that match the given
     * name.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void searchNoMatchingClothingProducts() {
        // arrange
        String search = "nope";
        when(clothingProductRepository.findClothingProductsByNameContainsIgnoreCase(search))
                .thenReturn(List.of());

        // act
        List<ClothingProduct> matchingClothingProducts =
                clothingProductService.getMatchingClothingProducts(search, null, null);

        // assert
        assertTrue(
                matchingClothingProducts.isEmpty(),
                "Clothing products matching the name search were incorrectly found.");
        verify(clothingProductRepository, times(1))
                .findClothingProductsByNameContainsIgnoreCase(search);
    }

    /**
     * Test running filter by size on clothing products, expecting no products that match the given
     * size.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void filterSizeNoMatchingClothingProducts() {
        // arrange
        List<ClothingItem.Size> sizes = List.of(ClothingItem.Size.XL, ClothingItem.Size.S);
        when(clothingProductRepository.findAll()).thenReturn(List.of(clothingProduct));

        // act
        List<ClothingProduct> matchingClothingProducts =
                clothingProductService.getMatchingClothingProducts(null, sizes, null);

        // assert
        assertTrue(
                matchingClothingProducts.isEmpty(),
                "Clothing products matching the size filters were incorrectly found.");
        verify(clothingProductRepository, times(1)).findAll();
    }

    /**
     * Test running filter by colour on clothing products, expecting no products that match the
     * given colour.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void filterColourNoMatchingClothingProducts() {
        // arrange
        List<ClothingItem.Colour> colours =
                List.of(ClothingItem.Colour.RED, ClothingItem.Colour.BLUE);
        when(clothingProductRepository.findAll()).thenReturn(List.of(clothingProduct));

        // act
        List<ClothingProduct> matchingClothingProducts =
                clothingProductService.getMatchingClothingProducts(null, null, colours);

        // assert
        assertTrue(
                matchingClothingProducts.isEmpty(),
                "Clothing products matching the colour filters were incorrectly found.");
        verify(clothingProductRepository, times(1)).findAll();
    }

    /**
     * Test running search and filter on clothing products, expecting a match for the given
     * (partial) name, size, and colour.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void matchingClothingProducts() {
        // arrange
        String name = "hood";
        List<ClothingItem.Size> sizes = List.of(ClothingItem.Size.M);
        List<ClothingItem.Colour> colours = List.of(ClothingItem.Colour.YELLOW);
        when(clothingProductRepository.findClothingProductsByNameContainsIgnoreCase(name))
                .thenReturn(List.of(clothingProduct));

        // act
        List<ClothingProduct> matchingClothingProducts =
                clothingProductService.getMatchingClothingProducts(name, sizes, colours);

        // assert
        assertIterableEquals(
                List.of(clothingProduct),
                matchingClothingProducts,
                "Clothing products matching search by name and filter by size and colour are incorrect.");
        verify(clothingProductRepository, times(1))
                .findClothingProductsByNameContainsIgnoreCase(name);
    }

    /**
     * Test deleting an existing ClothingProduct (valid request).
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void deleteClothingProductSuccess() {
        when(clothingProductRepository.findById(1)).thenReturn(Optional.of(clothingProduct));

        assertDoesNotThrow(() -> clothingProductService.deleteClothingProduct(1));

        verify(clothingProductRepository, times(1)).delete(clothingProduct);
    }

    /**
     * Test deleting a non-existent ClothingProduct (invalid request).
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void deleteClothingProductFailNonExistentClothingProduct() {
        when(clothingProductRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(
                FashionStoreException.class, () -> clothingProductService.deleteClothingProduct(1));
    }

    /**
     * Test creating a clothing product with valid parameters.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void createClothingProductSuccess() {
        when(clothingProductRepository.save(any(ClothingProduct.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        assertDoesNotThrow(
                () -> clothingProductService.createClothingProduct(this.clothingProductRequestDto),
                "Creating a clothing product with valid details should not throw an exception.");
    }

    /**
     * Updating a clothing product with valid parameters.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void updateClothingProductSuccess() {
        when(clothingProductRepository.findById(clothingProduct.getId()))
                .thenReturn(Optional.of(clothingProduct));
        when(clothingProductRepository.save(any(ClothingProduct.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        assertDoesNotThrow(
                () ->
                        clothingProductService.updateClothingProduct(
                                this.clothingProductRequestDto, this.clothingProduct.getId()),
                "Updating a clothing product with a valid request should not throw an exception.");
    }

    /**
     * Updating a clothing product with nonexistent clothing product ID.
     *
     * @author Jennifer You (jenni4u)
     */
    @Test
    void updateFailNonExistingClothingProduct() {
        int invalidId = clothingProduct.getId() + 1;
        when(clothingProductRepository.findById(invalidId)).thenReturn(Optional.empty());
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () ->
                                clothingProductService.updateClothingProduct(
                                        this.clothingProductRequestDto, invalidId),
                        "Updating a clothing product with a nonexistent ID should throw an exception.");

        assertEquals(
                HttpStatus.NOT_FOUND,
                e.getStatus(),
                "Invalid product ID should throw a NOT_FOUND exception.");
    }
}
