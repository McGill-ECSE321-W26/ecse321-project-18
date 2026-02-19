package ca.mcgill.ecse321.fashionstore.repository;

import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import org.springframework.data.repository.CrudRepository;

/** CRUDRepository for ClothingProduct with Integer as the ID type. */
public interface ClothingProductRepository extends CrudRepository<ClothingProduct, Integer> {

    /** Find by ID method for ClothingProduct. */
    ClothingProduct findClothingProductById(int id);

    /** Delete by ID method for ClothingProduct. */
    void deleteClothingProductById(int id);
}
