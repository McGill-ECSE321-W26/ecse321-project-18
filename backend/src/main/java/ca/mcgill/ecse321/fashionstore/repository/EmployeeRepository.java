package ca.mcgill.ecse321.fashionstore.repository;

import ca.mcgill.ecse321.fashionstore.model.Employee;
import org.springframework.data.repository.CrudRepository;

/** CRUDRepository for Employee with Integer as the ID type. */
public interface EmployeeRepository extends CrudRepository<Employee, Integer> {

    /** Find by ID method for Employee. */
    Employee findEmployeeById(int id);

    /** Delete by ID method for Employee. */
    void deleteEmployeeById(int id);
}
