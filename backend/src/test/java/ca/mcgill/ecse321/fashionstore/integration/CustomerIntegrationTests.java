package ca.mcgill.ecse321.fashionstore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.mcgill.ecse321.fashionstore.dto.CustomerRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.CustomerResponseDto;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/** Customer Service class tests. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureRestTestClient
class CustomerIntegrationTests {
    private static final String CUSTOMER_LOYALTY_PTS_URI =
            "/fashionstore/account/customer/{customerId}/loyalty";
    private static final String CUSTOMER_GET_URI = "/fashionstore/account/customer/{customerId}";
    private static final String ERROR_LOC = "$.errors";
    private static final String RESPONSE_NULL_ERROR = "Response body is null.";

    private static final String CUSTOMER_EMAIL = "customer@example.com";
    private static final String CUSTOMER_PASSWORD = "canthackthis";
    private static final String CUSTOMER_ADDRESS = "29 Fashion Dr";
    private static final int CUSTOMER_LOYALTY_PTS = 15;
    private static final int LOYALTY_PTS_INVALID = -1;

    private int customerId;

    @Autowired private RestTestClient client;

    @Autowired private CustomerRepository customerRepository;

    /** Setup method for Customer integration tests. */
    @BeforeAll
    public void setup() {
        // arrange
        Customer customer = createCustomer(CUSTOMER_EMAIL, CUSTOMER_PASSWORD, CUSTOMER_ADDRESS);
        customerId = customer.getId();
    }

    /** Cleanup method for Customer integration tests. */
    @AfterAll
    public void clearDatabase() {
        customerRepository.deleteAll();
    }

    /**
     * Integration test to update loyalty points with an invalid customer ID.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    @Order(0)
    void testUpdateLoyaltyPtsByInvalidId() {
        // arrange
        CustomerRequestDto body =
                new CustomerRequestDto(
                        CUSTOMER_EMAIL, CUSTOMER_PASSWORD, CUSTOMER_ADDRESS, CUSTOMER_LOYALTY_PTS);
        int invalidCustomerId = customerId - 1;

        // act
        client.put()
                .uri(CUSTOMER_LOYALTY_PTS_URI, invalidCustomerId)
                .body(body)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath(ERROR_LOC)
                .isEqualTo(String.format("Customer ID %d was not found.", invalidCustomerId));
    }

    /**
     * Integration test to update loyalty points with a valid customer ID and valid loyalty points.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    @Order(1)
    void testUpdateLoyaltyPtsByValidId() {
        // arrange
        CustomerRequestDto body =
                new CustomerRequestDto(
                        CUSTOMER_EMAIL, CUSTOMER_PASSWORD, CUSTOMER_ADDRESS, CUSTOMER_LOYALTY_PTS);

        // act
        CustomerResponseDto response =
                client.put()
                        .uri(CUSTOMER_LOYALTY_PTS_URI, customerId)
                        .body(body)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(CustomerResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // assert
        assertUpdateLoyaltyPtsValid(
                response, CUSTOMER_EMAIL, CUSTOMER_ADDRESS, CUSTOMER_LOYALTY_PTS);
    }

    /**
     * Integration test to update loyalty points with a valid customer ID but invalid loyalty
     * points.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    @Order(2)
    void testUpdateLoyaltyPtsInvalid() {
        // arrange
        CustomerRequestDto body =
                new CustomerRequestDto(
                        CUSTOMER_EMAIL, CUSTOMER_PASSWORD, CUSTOMER_ADDRESS, LOYALTY_PTS_INVALID);

        // act and assert
        client.put()
                .uri(CUSTOMER_LOYALTY_PTS_URI, customerId)
                .body(body)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath(ERROR_LOC)
                .isEqualTo("Number of loyalty points must be positive or zero.");
    }

    private Customer createCustomer(String email, String password, String address) {
        Customer customer = new Customer();

        customer.setEmail(email);
        customer.setPassword(password);
        customer.setAddress(address);

        return customerRepository.save(customer);
    }

    private void assertUpdateLoyaltyPtsValid(
            CustomerResponseDto response,
            String customerEmail,
            String customerAddress,
            int customerLoyaltyPts) {
        assertNotNull(response, RESPONSE_NULL_ERROR);
        assertEquals(
                customerEmail,
                response.email(),
                "Customer email should not be modified when updating loyalty points.");
        assertEquals(
                customerAddress,
                response.address(),
                "Customer address should not be modified when updating loyalty points.");
        assertEquals(
                customerLoyaltyPts,
                (int) response.numOfLoyaltyPoints(),
                "The number of loyalty points was not updated.");
    }

    /**
     * Helper for: Integration test to retrieve a customer by a valid ID.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    private void assertCustomerDetails(CustomerResponseDto response) {
        assertEquals(
                CUSTOMER_EMAIL, response.email(), "Customer email does not match expected value.");
        assertEquals(
                CUSTOMER_ADDRESS,
                response.address(),
                "Customer address does not match expected value.");
        assertEquals(
                CUSTOMER_LOYALTY_PTS,
                (int) response.numOfLoyaltyPoints(),
                "Customer loyalty points do not match expected value.");
    }

    /**
     * Integration test to retrieve a customer by a valid ID.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    @Order(3)
    void testGetCustomerByValidId() {
        CustomerResponseDto response =
                client.get()
                        .uri(CUSTOMER_GET_URI, customerId)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(CustomerResponseDto.class)
                        .returnResult()
                        .getResponseBody();
        assertNotNull(response, RESPONSE_NULL_ERROR);
        assertCustomerDetails(response);
    }

    /**
     * Integration test to retrieve a customer using an invalid ID.
     *
     * @author Kenneth Wang (KennethWang6)
     */
    @Test
    @Order(4)
    void testGetCustomerByInvalidId() {
        int invalidCustomerId = customerId + 28;

        client.get()
                .uri(CUSTOMER_GET_URI, invalidCustomerId)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath(ERROR_LOC)
                .isEqualTo(String.format("Customer ID %d was not found.", invalidCustomerId));
    }
}
