package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/** Employee Service class. */
@Service
@Validated
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    /**
     * EmployeeService constructor.
     *
     * @param employeeRepository EmployeeRepository required to access the database.
     * @author Flavie Qin
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * Service method to get the information of an employee.
     *
     * @param id ID of employee
     * @return Employee instance
     * @author Flavie Qin
     */
    @Transactional
    public Employee getEmployee(int id) {
        return Utils.findEmployeeById(employeeRepository, id);
    }
}
