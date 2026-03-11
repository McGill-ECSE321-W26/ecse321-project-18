package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.mcgill.ecse321.fashionstore.controller.AccountController;
import ca.mcgill.ecse321.fashionstore.dto.AccountRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.AccountResponseDto;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.model.Owner;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OwnerRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/** Account Service class integration tests. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureRestTestClient
class AccountServiceIntegrationTests {
    @Autowired private AccountController accountController;
    @Autowired private RestTestClient client;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private CustomerRepository customerRepository;

    // URI
    private static final String accountLoginUri = "/fashionstore/account/login";

    // Error messages
    private static final String responseNullError = "Response body is null";

    /** Setup for accountService integration tests. */
    @BeforeAll
    void setup() {
        Owner owner = new Owner();
        owner.setEmail("owner@fashionstore.com");
        owner.setPassword("owner123");
        Employee employee = new Employee();
        employee.setEmail("employee@fashionstore.com");
        employee.setPassword("employee123");
        Customer customer = new Customer();
        customer.setEmail("customer@fashionstore.com");
        customer.setPassword("customer123");
        ownerRepository.save(owner);
        employeeRepository.save(employee);
        customerRepository.save(customer);
    }

    /** Clear database after all tests */
    @AfterAll
    void clearDatabase() {
        ownerRepository.deleteAll();
        employeeRepository.deleteAll();
        customerRepository.deleteAll();
    }

    /** Test successful owner account login */
    @Test
    void accountLogin() {
        // Arrange
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("owner@fashionstore.com", "owner123");

        // Act
        AccountResponseDto response =
                client.post()
                        .uri(accountLoginUri)
                        .body(accountRequestDto)
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .expectBody(AccountResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Asserts
        assertNotNull(response, responseNullError);
    }
}
