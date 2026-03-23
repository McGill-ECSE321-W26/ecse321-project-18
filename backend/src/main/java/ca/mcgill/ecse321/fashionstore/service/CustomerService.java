package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.CustomerRequestDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
     * Service method to get the information of a customer.
     *
     * @param id ID of customer
     * @return Customer instance
     * @author Flavie Qin
     */
    @Transactional
    public Customer getCustomer(int id) {
        return Utils.findCustomerById(customerRepository, id);
    }

    /**
     * Service method to update a customer's loyalty points.
     *
     * @param id ID of customer whose loyalty points will be updated.
     * @param customerRequestDto Request DTO for customer.
     * @return The updated customer.
     * @author Carolyn Wu (cw118)
     */
    @Transactional
    public Customer updateCustomerLoyaltyPts(int id, @Valid CustomerRequestDto customerRequestDto) {
        Customer customer = Utils.findCustomerById(customerRepository, id);
        int updatedLoyaltyPts = customerRequestDto.numOfLoyaltyPoints();

        if (updatedLoyaltyPts < 0) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST, "Number of loyalty points must be positive or zero.");
        }

        customer.setNumLoyaltyPoints(updatedLoyaltyPts);

        return customerRepository.save(customer);
    }

    /**
     * Service method to update a customer's information.
     *
     * @param id ID of customer who is being updated.
     * @param customerRequestDto Request DTO for customer.
     * @return The updated customer.
     * @author Cyrus Fung (cfung89)
     */
    @Transactional
    public Customer updateCustomer(int id, @Valid CustomerRequestDto customerRequestDto) {
        Customer customer = Utils.findCustomerById(customerRepository, id);
        customer.setPassword(customerRequestDto.password());
        customer.setAddress(customerRequestDto.address());
        return customerRepository.save(customer);
    }
}
