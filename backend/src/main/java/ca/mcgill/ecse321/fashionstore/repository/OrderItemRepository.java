package ca.mcgill.ecse321.fashionstore.repository;

import ca.mcgill.ecse321.fashionstore.model.OrderItem;
import org.springframework.data.repository.CrudRepository;

/** CRUDRepository for OrderItem with Integer as the ID type. */
public interface OrderItemRepository extends CrudRepository<OrderItem, Integer> {

    /** Find by ID method for OrderItem. */
    OrderItem findOrderItemById(int id);

    /** Delete by ID method for OrderItem. */
    void deleteOrderItemById(int id);
}
