package ca.mcgill.ecse321.fashionstore.repository;

import ca.mcgill.ecse321.fashionstore.model.Account;
import org.springframework.data.repository.CrudRepository;

/** CRUDRepository for Account with Integer as the ID type. */
public interface AccountRepository extends CrudRepository<Account, String> {

    /** Find by ID method for Account. */
    Account findAccountByEmail(String email);
}
