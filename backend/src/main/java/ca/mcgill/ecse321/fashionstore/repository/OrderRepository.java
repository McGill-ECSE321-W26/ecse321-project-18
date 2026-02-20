package ca.mcgill.ecse321.fashionstore.repository;

import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.Order.State;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/** CRUDRepository for Order with Integer as the ID type. */
public interface OrderRepository extends CrudRepository<Order, Integer> {

    /** Find by ID method for Order. */
    Order findOrderById(int id);

    /** Find by State method for Order. */
    Order findOrderByState(State state);

    /** Delete by ID method for Order. */
    void deleteOrderById(int id);

    /** Removes employee reference from all orders assigned to the given employee. */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE store_order SET employee_email = null WHERE employee_email = :email", nativeQuery = true)
    void removeEmployeeReference(@Param("email") String email);
}
