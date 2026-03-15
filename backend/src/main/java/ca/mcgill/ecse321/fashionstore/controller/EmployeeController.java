package ca.mcgill.ecse321.fashionstore.controller;

import ca.mcgill.ecse321.fashionstore.dto.EmployeeResponseDto;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.service.EmployeeService;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller for Employee REST API endpoints. */
@RestController
@SuppressFBWarnings("SPRING_ENDPOINT")
public class EmployeeController {
    private EmployeeService employeeService;

    /**
     * Constructor for EmployeeController.
     *
     * @param employeeService Employee service class.
     * @author Flavie Qin
     */
    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Gets the information of an employee.
     *
     * @param employeeId Employee ID.
     * @return A DTO representing the employee.
     * @author Flavie Qin
     */
    @PutMapping("/fashionstore/account/employee/{employeeId}")
    public EmployeeResponseDto getEmployee(@PathVariable int employeeId) {
        Employee employee = employeeService.getEmployee(employeeId);

        return new EmployeeResponseDto(employee);
    }
}
