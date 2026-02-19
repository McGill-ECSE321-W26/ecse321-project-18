package ca.mcgill.ecse321.fashionstore.repository;

import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ClothingProductRepositoryTests {
    @Autowired
    private ClothingProductRepository clothingProductRepository;

    @AfterEach
    public void clearDatabase() {
        clothingProductRepository.deleteAll();
    }

    @Test
    public void testPersistAndLoadClothingProduct() {
        // Create clothingProduct
        String name = "Crewneck";
        float price = 59.99f;
        String image = "someimagepath.jpg";
        ClothingProduct newClothingProduct = new ClothingProduct();
        newClothingProduct.setName(name);
        newClothingProduct.setPrice(price);
        newClothingProduct.setImage(image);

        // Save the clothing product
        clothingProductRepository.save(newClothingProduct);
        int id = newClothingProduct.getId();

        // Read person from database
        ClothingProduct clothingProductFromDB = clothingProductRepository.findClothingProductById(id);

        // Assert correct response
        assertNotNull(clothingProductFromDB);
        assertEquals(name, clothingProductFromDB.getName());
        assertEquals(price, clothingProductFromDB.getPrice());
        assertEquals(image, clothingProductFromDB.getImage());
    }
}