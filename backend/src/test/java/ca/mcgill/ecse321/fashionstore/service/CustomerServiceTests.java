package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.fashionstore.dto.CustomerRequestDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import java.util.Optional;
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

/** Customer Service class tests. */
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

    /** Setup function for Customer service layer tests. */
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
    void updateLoyaltyPtsByInvalidId() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.empty());

        CustomerRequestDto customerRequestDto =
                new CustomerRequestDto(
                        CUSTOMER_EMAIL, CUSTOMER_PASSWORD, CUSTOMER_ADDRESS, CUSTOMER_LOYALTY_PTS);

        // assert
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () ->
                                customerService.updateCustomerLoyaltyPts(
                                        CUSTOMER_ID, customerRequestDto));

        assertEquals(
                HttpStatus.NOT_FOUND,
                e.getStatus(),
                "HTTP status is not NOT_FOUND after invalid customer ID request.");
        assertEquals(
                String.format("Customer ID %d was not found.", CUSTOMER_ID),
                e.getMessage(),
                "HTTP message is incorrect after invalid customer ID request.");
    }

    /**
     * Service layer test for updating a customer's loyalty points with a valid customer ID and
     * number of points.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void updateLoyaltyPtsValid() {
        // arrange
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        // act
        CustomerRequestDto customerRequestDto =
                new CustomerRequestDto(
                        CUSTOMER_EMAIL, CUSTOMER_PASSWORD, CUSTOMER_ADDRESS, CUSTOMER_LOYALTY_PTS);
        Customer updatedCustomer =
                customerService.updateCustomerLoyaltyPts(CUSTOMER_ID, customerRequestDto);

        // assert
        assertUpdateLoyaltyPtsValid(updatedCustomer);
        verifyUpdateLoyaltyPtsValid();
    }

    /**
     * Service layer test for updating a customer's loyalty points with a valid customer ID but
     * invalid number of points.
     */
    @Test
    void updateLoyaltyPtsInvalid() {
        // arrange
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

        CustomerRequestDto customerRequestDto =
                new CustomerRequestDto(
                        CUSTOMER_EMAIL, CUSTOMER_PASSWORD, CUSTOMER_ADDRESS, LOYALTY_PTS_INVALID);

        // assert
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () ->
                                customerService.updateCustomerLoyaltyPts(
                                        CUSTOMER_ID, customerRequestDto));

        assertUpdateLoyaltyPtsInvalid(e);
    }

    private void assertUpdateLoyaltyPtsValid(Customer updatedCustomer) {
        assertEquals(
                CUSTOMER_LOYALTY_PTS,
                updatedCustomer.getNumLoyaltyPoints(),
                "Customer's loyalty points were not updated correctly.");
        assertEquals(CUSTOMER_ID, updatedCustomer.getId(), "Customer ID is incorrect.");
        assertEquals(CUSTOMER_EMAIL, updatedCustomer.getEmail(), "Customer email is incorrect.");
        assertEquals(
                CUSTOMER_PASSWORD,
                updatedCustomer.getPassword(),
                "Customer password is incorrect.");
        assertEquals(
                CUSTOMER_ADDRESS, updatedCustomer.getAddress(), "Customer address is incorrect.");
    }

    private void verifyUpdateLoyaltyPtsValid() {
        verify(customerRepository, times(1))
                .save(
                        argThat(
                                (Customer c) ->
                                        c.getId() == CUSTOMER_ID
                                                && CUSTOMER_EMAIL.equals(c.getEmail())
                                                && CUSTOMER_PASSWORD.equals(c.getPassword())
                                                && CUSTOMER_ADDRESS.equals(c.getAddress())
                                                && c.getNumLoyaltyPoints()
                                                        == CUSTOMER_LOYALTY_PTS));
    }

    private void assertUpdateLoyaltyPtsInvalid(FashionStoreException e) {
        assertEquals(
                HttpStatus.BAD_REQUEST,
                e.getStatus(),
                "HTTP status is not BAD_REQUEST after customer request DTO with invalid loyalty points.");
        assertEquals(
                "Number of loyalty points must be positive or zero.",
                e.getMessage(),
                "HTTP message is incorrect after customer request DTO with invalid loyalty points.");
    }

    private Customer createCustomer(int id, String email, String password, String address) {
        Customer newCustomer = new Customer();
        newCustomer.setId(id);
        newCustomer.setEmail(email);
        newCustomer.setPassword(password);
        newCustomer.setAddress(address);

        return newCustomer;
    }

    /**
     * Helper for: Service layer test for retrieving a customer by a valid ID.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    private void assertCustomerDetails(Customer expected, Customer actual) {
        assertEquals(
                expected.getId(), actual.getId(), "Customer ID does not match expected value.");
        assertEquals(
                expected.getEmail(),
                actual.getEmail(),
                "Customer email does not match expected value.");
        assertEquals(
                expected.getPassword(),
                actual.getPassword(),
                "Customer password does not match expected value.");
        assertEquals(
                expected.getAddress(),
                actual.getAddress(),
                "Customer address does not match expected value.");
        assertEquals(
                expected.getNumLoyaltyPoints(),
                actual.getNumLoyaltyPoints(),
                "Customer loyalty points do not match expected value.");
    }

    /**
     * Service layer test for retrieving a customer by a valid ID.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void getCustomerByValidIdSuccess() {
        when(customerRepository.findById(CUSTOMER_ID)).thenReturn(Optional.of(customer));
        Customer response =
                assertDoesNotThrow(
                        () -> customerService.getCustomer(CUSTOMER_ID),
                        "Trying to get a valid, existing customer throws an exception.");

        assertNotNull(response, "Returned customer should not be null.");
        assertCustomerDetails(customer, response);

        verify(customerRepository, times(1)).findById(CUSTOMER_ID);
    }

    /**
     * Service layer test for retrieving a customer by an invalid ID.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    void getCustomerByInvalidIdFail() {
        int id = customer.getId() + 28;
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                FashionStoreException.class,
                () -> customerService.getCustomer(id),
                "Trying to find non existent customer ID should not find anything.");
    }
}
