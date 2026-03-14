package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.CustomerRequestDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Customer Service class tests.
 */
@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class CustomerServiceTests {
    @Mock private CustomerRepository customerRepository;

    @InjectMocks CustomerService customerService;

    private static final int CUSTOMER_ID = 3;
    private static final String CUSTOMER_EMAIL = "customer@example.com";
    private static final String CUSTOMER_PASSWORD = "securelystoredpassword";
    private static final String CUSTOMER_ADDRESS = "24 Fashion Street";
    private static final int CUSTOMER_LOYALTY_PTS = 26;
    private static final int LOYALTY_PTS_INVALID = -1;

    private Customer customer;

    /**
     * Setup function for Customer service layer tests.
     */
    @BeforeEach
    void setup() {
        customer = createCustomer(CUSTOMER_ID, CUSTOMER_EMAIL, CUSTOMER_PASSWORD, CUSTOMER_ADDRESS);
    }

    @AfterEach
    void clearDatabase() {
        customerRepository.deleteAll();
    }

    /**
     * Service layer test for updating a customer's loyalty points with invalid customer ID.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void testUpdateLoyaltyPtsByInvalidId() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        CustomerRequestDto customerRequestDto = new CustomerRequestDto(
            CUSTOMER_EMAIL, CUSTOMER_PASSWORD, CUSTOMER_ADDRESS, CUSTOMER_LOYALTY_PTS
        );

        // assert
        FashionStoreException e = assertThrows(
            FashionStoreException.class,
            () -> customerService.updateCustomerLoyaltyPts(CUSTOMER_ID, customerRequestDto)
        );

        assertEquals(
            HttpStatus.NOT_FOUND,
            e.getStatus(),
            "HTTP status is not NOT_FOUND after invalid customer ID request."
        );
        assertEquals(
            String.format("Customer ID %d was not found.", CUSTOMER_ID),
            e.getMessage(),
            "HTTP message is incorrect after invalid customer ID request."
        );
    }

    /**
     * Service layer test for updating a customer's loyalty points with a valid customer ID and number of points.
     * Invalid (i.e. negative) number of points is already not permitted, a rule enforced by the CustomerRequestDto class.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void testUpdateLoyaltyPtsValid() {
        // arrange
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class)))
            .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        // act
        CustomerRequestDto customerRequestDto = new CustomerRequestDto(CUSTOMER_EMAIL, CUSTOMER_PASSWORD, CUSTOMER_ADDRESS, CUSTOMER_LOYALTY_PTS);
        Customer updatedCustomer = customerService.updateCustomerLoyaltyPts(CUSTOMER_ID, customerRequestDto);

        // assert
        assertEquals(
            CUSTOMER_LOYALTY_PTS,
            updatedCustomer.getNumLoyaltyPoints(),
            "Customer's loyalty points were not updated correctly."
        );
        assertEquals(
            CUSTOMER_ID,
            updatedCustomer.getId(),
            "Customer ID is incorrect."
        );
        assertEquals(
            CUSTOMER_EMAIL,
            updatedCustomer.getEmail(),
            "Customer email is incorrect."
        );
        assertEquals(
            CUSTOMER_PASSWORD,
            updatedCustomer.getPassword(),
            "Customer password is incorrect."
        );
        assertEquals(
            CUSTOMER_ADDRESS,
            updatedCustomer.getAddress(),
            "Customer address is incorrect."
        );
        verifyUpdateLoyaltyPtsValid();
    }

    private void verifyUpdateLoyaltyPtsValid() {
        verify(customerRepository, times(1))
            .save(
                argThat(
                    (Customer c) ->
                        c.getId() == CUSTOMER_ID
                    && c.getEmail().equals(CUSTOMER_EMAIL)
                    && c.getPassword().equals(CUSTOMER_PASSWORD)
                    && c.getAddress().equals(CUSTOMER_ADDRESS)
                    && c.getNumLoyaltyPoints() == CUSTOMER_LOYALTY_PTS
                )
            );
    }

    private Customer createCustomer(int id, String email, String password, String address) {
        Customer newCustomer = new Customer();
        newCustomer.setId(id);
        newCustomer.setEmail(email);
        newCustomer.setPassword(password);
        newCustomer.setAddress(address);

        return newCustomer;
    }
}
