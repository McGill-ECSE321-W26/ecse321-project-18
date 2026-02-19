package ca.mcgill.ecse321.fashionstore.repository;

import ca.mcgill.ecse321.fashionstore.model.Customer;
import org.springframework.data.repository.CrudRepository;

/** CRUDRepository for Customer with Integer as the ID type. */
public interface CustomerRepository extends CrudRepository<Customer, String> {

    /** Find by ID method for Customer. */
    Customer findCustomerByEmail(String email);
}
