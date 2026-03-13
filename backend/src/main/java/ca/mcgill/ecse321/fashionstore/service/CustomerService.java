package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.CustomerRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.CustomerResponseDto;
import ca.mcgill.ecse321.fashionstore.dto.AccountRequestDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.repository.AccountRepository;
import org.springframework.http.HttpStatus;
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
    private final AccountRepository accountRepository;

    /**
     * CustomerService constructor.
     *
     * @param customerRepository CustomerRepository required to access the database.
     * @author Carolyn Wu (cw118), Aurore Zhang (ororio0)
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CustomerService(CustomerRepository customerRepository,
                           AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Service method to update a customer's loyalty points.
     *
     * @param id ID of customer whose loyalty points will be updated
     * @param customerRequestDto Request DTO for customer
     * @return DTO for the updated customer
     * @author Carolyn Wu (cw118)
     */
    @Transactional
    public CustomerResponseDto updateCustomerLoyaltyPts(
            int id, @Valid CustomerRequestDto customerRequestDto) {
        Customer customer = Utils.findCustomerById(customerRepository, id);
        customer.setNumLoyaltyPoints(customerRequestDto.numOfLoyaltyPoints());

        CustomerResponseDto dto = new CustomerResponseDto(customer);
        return dto;
    }

    /**
     * Service method to create a new customer account.
     *
     * @param accountRequestDto Request DTO containing email and password.
     * @return The new created Customer instance.
     * @author Aurore Zhang (ororio0)
     */
    @Transactional
    public Customer createCustomer(@Valid AccountRequestDto accountRequestDto) {
        if (accountRepository.existsByEmail(accountRequestDto.email())) {
            throw new FashionStoreException(
                    HttpStatus.CONFLICT,
                    String.format(
                            "Account with email %s already exists.", accountRequestDto.email()));
        }
        Customer customer = new Customer();
        customer.setEmail(accountRequestDto.email());
        customer.setPassword(accountRequestDto.password());
        return customerRepository.save(customer);
    }
}
