package ca.mcgill.ecse321.fashionstore.controller;

import ca.mcgill.ecse321.fashionstore.dto.CustomerRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.CustomerResponseDto;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.service.CustomerService;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Controller for Customer REST API endpoints. */
@CrossOrigin
@RestController
@SuppressFBWarnings("SPRING_ENDPOINT")
public class CustomerController {
    private CustomerService customerService;

    /**
     * Constructor for CustomerController.
     *
     * @param customerService Customer service class.
     * @author Carolyn Wu (cw118)
     */
    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Gets the information of a customer.
     *
     * @param customerId Customer ID.
     * @return A DTO representing the customer.
     * @author Flavie Qin
     */
    @GetMapping("/fashionstore/account/customer/{customerId}")
    public CustomerResponseDto getCustomer(@PathVariable int customerId) {
        Customer customer = customerService.getCustomer(customerId);
        return new CustomerResponseDto(customer);
    }

    /**
     * Updates the number of loyalty points for a customer.
     *
     * @param customerId Customer ID.
     * @param customerRequestDto DTO specifying the new number of loyalty points.
     * @return A DTO representing the customer with updated loyalty points.
     * @author Carolyn Wu (cw118)
     */
    @PutMapping("/fashionstore/account/customer/{customerId}/loyalty")
    public CustomerResponseDto updateCustomerLoyaltyPts(
            @PathVariable int customerId, @RequestBody CustomerRequestDto customerRequestDto) {
        Customer customer =
                customerService.updateCustomerLoyaltyPts(customerId, customerRequestDto);
        CustomerResponseDto dto = new CustomerResponseDto(customer);

        return dto;
    }
}
