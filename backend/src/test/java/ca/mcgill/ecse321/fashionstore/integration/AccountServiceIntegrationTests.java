package ca.mcgill.ecse321.fashionstore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

/** Account Service class integration tests. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureRestTestClient
class AccountServiceIntegrationTests {
    @Autowired private AccountController accountController;
    @Autowired private RestTestClient client;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private CustomerRepository customerRepository;

    private Owner owner;
    private Employee employee;
    private Customer customer;

    // URI
    private static final String accountLoginUri = "/fashionstore/account/login";
    private static final String accountCreateEmployeeUri = "/fashionstore/account/employee";
    private static final String accountCreateCustomerUri = "/fashionstore/account/customer";

    // Error messages
    private static final String responseNullError = "Response body is null";

    /** Setup for accountService integration tests. */
    @BeforeAll
    void setup() {
        // initialize
        Owner owner = new Owner();
        owner.setEmail("owner@fashionstore.com");
        owner.setPassword("owner123");
        this.owner = owner;
        Employee employee = new Employee();
        employee.setEmail("employee@fashionstore.com");
        employee.setPassword("employee123");
        this.employee = employee;
        Customer customer = new Customer();
        customer.setEmail("customer@fashionstore.com");
        customer.setPassword("customer123");
        this.customer = customer;

        // Save to repository
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

    /**
     * Test successful owner account login.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void accountOwnerLogin() {
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
                        .isOk()
                        .expectBody(AccountResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Asserts
        assertNotNull(response, responseNullError);
        assertEquals(response.email(), owner.getEmail(), "Owner login response has wrong email.");
        assertEquals(response.id(), owner.getId(), "Owner login response has wrong ID.");
    }

    /**
     * Test successful employee account login.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void accountEmployeeLogin() {
        // Arrange
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("employee@fashionstore.com", "employee123");

        // Act
        AccountResponseDto response =
                client.post()
                        .uri(accountLoginUri)
                        .body(accountRequestDto)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(AccountResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Asserts
        assertNotNull(response, responseNullError);
        assertEquals(
                response.email(), employee.getEmail(), "Employee login response has wrong email.");
        assertEquals(response.id(), employee.getId(), "Employee login response has wrong ID.");
    }

    /**
     * Test successful customer account login.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void accountCustomerLogin() {
        // Arrange
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("customer@fashionstore.com", "customer123");

        // Act
        AccountResponseDto response =
                client.post()
                        .uri(accountLoginUri)
                        .body(accountRequestDto)
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(AccountResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Asserts
        assertNotNull(response, responseNullError);
        assertEquals(
                response.email(), customer.getEmail(), "Customer login response has wrong email.");
        assertEquals(response.id(), customer.getId(), "Customer login response has wrong ID.");
    }

    /**
     * Test unsuccessful owner account login.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void failAccountOwnerLogin() {
        // Arrange
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("owner@fashionstore.com", "owner14354");

        // Act
        AccountResponseDto response =
                client.post()
                        .uri(accountLoginUri)
                        .body(accountRequestDto)
                        .exchange()
                        .expectStatus()
                        .isBadRequest()
                        .expectBody(AccountResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Asserts
        assertNotNull(response, responseNullError);
        assertNull(response.email(), "Failed owner login should have null email in response.");
    }

    /**
     * Test unsuccessful customer account login.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void failAccountCustomerLogin() {
        // Arrange
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("customer@fashionstore.com", "customer124");

        // Act
        AccountResponseDto response =
                client.post()
                        .uri(accountLoginUri)
                        .body(accountRequestDto)
                        .exchange()
                        .expectStatus()
                        .isBadRequest()
                        .expectBody(AccountResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Asserts
        assertNotNull(response, responseNullError);
        assertNull(response.email(), "Failed customer login should have null email in response.");
    }

    /**
     * Test unsuccessful employee account login.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void failAccountEmployeeLogin() {
        // Arrange
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("employee@fashionstore.com", "employee1234");

        // Act
        AccountResponseDto response =
                client.post()
                        .uri(accountLoginUri)
                        .body(accountRequestDto)
                        .exchange()
                        .expectStatus()
                        .isBadRequest()
                        .expectBody(AccountResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Asserts
        assertNotNull(response, responseNullError);
        assertNull(response.email(), "Failed employee login should have null email in response.");
    }

    /**
     * Test successful employee account creation.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void createEmployeeAccount() {
        // Arrange
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("newemployee@fashionstore.com", "employee001");

        // Act
        AccountResponseDto response =
                client.post()
                        .uri(accountCreateEmployeeUri)
                        .body(accountRequestDto)
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .expectBody(AccountResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertNotNull(response, responseNullError);
        assertNotNull(response.id(), "Created employee account response has null ID.");
        assertEquals("newemployee@fashionstore.com", response.email(),
                "Created employee account response has wrong email.");
        assertNull(response.accountType(),
                "Created employee account response should have null account type.");
    }

    /**
     * Test unsuccessful employee account creation when duplicated email.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void failCreateEmployeeAccountDuplicateEmail() {
        // Arrange
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("employee@fashionstore.com", "employee001");

        // Act
        AccountResponseDto response =
                client.post()
                        .uri(accountCreateEmployeeUri)
                        .body(accountRequestDto)
                        .exchange()
                        .expectStatus()
                        .isEqualTo(409)
                        .expectBody(AccountResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertNotNull(response, responseNullError);
        assertNull(response.id(), "Duplicate employee account creation should have null ID.");
        assertNull(response.email(), "Duplicate employee account creation should have null email.");
        assertNull(response.accountType(),
                "Duplicate employee account creation should have null account type.");
    }

    /**
     * Test successful customer account creation.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void createCustomerAccount() {
        // Arrange
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("newcustomer@fashionstore.com", "anonymous007");

        // Act
        AccountResponseDto response =
                client.post()
                        .uri(accountCreateCustomerUri)
                        .body(accountRequestDto)
                        .exchange()
                        .expectStatus()
                        .isCreated()
                        .expectBody(AccountResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertNotNull(response, responseNullError);
        assertNotNull(response.id(), "Created customer account response has null ID.");
        assertEquals("newcustomer@fashionstore.com", response.email(),
                "Created customer account response has wrong email.");
        assertNull(response.accountType(),
                "Created customer account response should have null account type.");
    }

    /**
     * Test unsuccessful customer account creation when duplicated email.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void failCreateCustomerAccountDuplicateEmail() {
        // Arrange
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("customer@fashionstore.com", "customer456");

        // Act
        AccountResponseDto response =
                client.post()
                        .uri(accountCreateCustomerUri)
                        .body(accountRequestDto)
                        .exchange()
                        .expectStatus()
                        .isEqualTo(409)
                        .expectBody(AccountResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertNotNull(response, responseNullError);
        assertNull(response.id(), "Duplicate customer account creation should have null ID.");
        assertNull(response.email(), "Duplicate customer account creation should have null email.");
        assertNull(response.accountType(),
                "Duplicate customer account creation should have null account type.");
    }
}
