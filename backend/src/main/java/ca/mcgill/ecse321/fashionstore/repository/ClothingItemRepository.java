package ca.mcgill.ecse321.fashionstore.repository;

import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import org.springframework.data.repository.CrudRepository;

/** CRUDRepository for ClothingItem with Integer as the ID type. */
public interface ClothingItemRepository extends CrudRepository<ClothingItem, Integer> {

    /** Find by ID method for ClothingItem. */
    ClothingItem findClothingItemById(int id);

    /** Delete by ID method for ClothingItem. */
    void deleteClothingItemById(int id);

    /** Remove all items that have zero stock. */
    void deleteByNumInStock(int amount);
}
