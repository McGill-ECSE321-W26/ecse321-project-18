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
    private static final String customerLoyaltyPtsUri =
            "/fashionstore/account/customer/{customerId}/loyalty";
    private static final String errorLoc = "$.errors";
    private static final String RESPONSE_NULL_ERROR = "Response body is null.";

    private static final String CUSTOMER_EMAIL = "customer@example.com";
    private static final String CUSTOMER_PASSWORD = "canthackthis";
    private static final String CUSTOMER_ADDRESS = "29 Fashion Dr";
    private static final int CUSTOMER_LOYALTY_PTS = 15;

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
                .uri(customerLoyaltyPtsUri, invalidCustomerId)
                .body(body)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath(errorLoc)
                .isEqualTo(String.format("Customer ID %d was not found.", invalidCustomerId));
    }

    /**
     * Integration test to update loyalty points with a valid customer ID.
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
                        .uri(customerLoyaltyPtsUri, customerId)
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
}
