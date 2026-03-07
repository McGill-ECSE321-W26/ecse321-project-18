package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.CustomerResponseDto;
import ca.mcgill.ecse321.fashionstore.dto.LoyaltyPtsRequestDto;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/** Customer Service class. */
@Service
@Validated
public class CustomerService {
    private final CustomerRepository customerRepository;

    /**
     * CustomerService constructor.
     *
     * @param customerRepository CustomerRepository required to access the database.
     * @author Carolyn Wu (cw118)
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Service method to update a customer's loyalty points.
     *
     * @param id ID of customer whose loyalty points will be updated
     * @param loyaltyPtsRequestDto Request DTO for loyalty points
     * @return DTO for the updated customer
     * @author Carolyn Wu (cw118)
     */
    @Transactional
    public CustomerResponseDto updateCustomerLoyaltyPts(
            int id, @Valid LoyaltyPtsRequestDto loyaltyPtsRequestDto) {
        Customer customer = Utils.findCustomerById(customerRepository, id);
        customer.setNumLoyaltyPoints(loyaltyPtsRequestDto.numOfLoyaltyPoints());

        CustomerResponseDto dto = new CustomerResponseDto(customer);
        return dto;
    }
}
