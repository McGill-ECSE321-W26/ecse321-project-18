package ca.mcgill.ecse321.fashionstore.controller;

import ca.mcgill.ecse321.fashionstore.dto.CustomerResponseDto;
import ca.mcgill.ecse321.fashionstore.dto.LoyaltyPtsRequestDto;
import ca.mcgill.ecse321.fashionstore.service.CustomerService;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Customer REST API endpoints.
 */
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
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Updates the number of loyalty points for a customer.
     *
     * @param customerId Customer ID.
     * @param loyaltyPtsRequestDto DTO specifying the new number of loyalty points.
     * @return A DTO representing the customer with updated loyalty points.
     * @author Carolyn Wu (cw118)
     */
    @PutMapping("/fashionstore/account/customer/{customerId}/loyalty")
    public CustomerResponseDto updateCustomerLoyaltyPts(
        @PathVariable int customerId,
        @RequestBody LoyaltyPtsRequestDto loyaltyPtsRequestDto) {
        return customerService.updateCustomerLoyaltyPts(customerId, loyaltyPtsRequestDto);
    }
}
