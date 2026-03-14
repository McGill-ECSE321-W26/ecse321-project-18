package ca.mcgill.ecse321.fashionstore.integration;

import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Customer Service class tests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureRestTestClient
class CustomerIntegrationTests {
    private static final String customerLoyaltyPtsUri = "/fashionstore/account/customer/{customerId}/loyalty";

    private int customerId1;
    private int customerId2;

    @Autowired private RestTestClient client;

    @Autowired private CustomerRepository customerRepository;
}
