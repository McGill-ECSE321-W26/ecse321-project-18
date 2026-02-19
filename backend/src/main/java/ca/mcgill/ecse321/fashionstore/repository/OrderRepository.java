package ca.mcgill.ecse321.fashionstore.repository;

import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.Order.State;
import org.springframework.data.repository.CrudRepository;

/** CRUDRepository for Order with Integer as the ID type. */
public interface OrderRepository extends CrudRepository<Order, Integer> {

    /** Find by ID method for Order. */
    Order findOrderById(int id);

    /** Find by State method for Order. */
    Order findOrderByState(State state);

    /** Delete by ID method for Order. */
    void deleteOrderById(int id);
}
