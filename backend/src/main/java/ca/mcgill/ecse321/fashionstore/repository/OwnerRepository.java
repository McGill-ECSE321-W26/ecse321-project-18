package ca.mcgill.ecse321.fashionstore.repository;

import ca.mcgill.ecse321.fashionstore.model.Owner;
import org.springframework.data.repository.CrudRepository;

/** CRUDRepository for Owner with Integer as the ID type. */
public interface OwnerRepository extends CrudRepository<Owner, String> {

    /** Find by ID method for Owner. */
    Owner findOwnerByEmail(String email);
}
