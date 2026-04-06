package ca.mcgill.ecse321.fashionstore.repository;

import ca.mcgill.ecse321.fashionstore.model.ShoppingCartItem;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

/** CRUDRepository for ShoppingCartItem with Integer as the ID type. */
public interface ShoppingCartItemRepository extends CrudRepository<ShoppingCartItem, Integer> {

    /** Find by ID method for ShoppingCartItem. */
    ShoppingCartItem findShoppingCartItemById(int id);

    /** Find by CustomerID and ClothingItemID method for ShoppingCartItem (unique). */
    Optional<ShoppingCartItem> findByCustomerIdAndClothingItemId(
            int customerId, int clothingItemId);
}
