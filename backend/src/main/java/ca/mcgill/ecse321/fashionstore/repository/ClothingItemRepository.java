package ca.mcgill.ecse321.fashionstore.repository;

import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem.Colour;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem.Size;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/** CRUDRepository for ClothingItem with Integer as the ID type. */
public interface ClothingItemRepository extends CrudRepository<ClothingItem, Integer> {

    /** Find by ID method for ClothingItem. */
    ClothingItem findClothingItemById(int id);

    /** Find by ClothingProductId and Colour and Size method for ClothingItem (unique). */
    Optional<ClothingItem> findByClothingProductIdAndColourAndSize(
            int clothingProductId, Colour colour, Size size);
}
