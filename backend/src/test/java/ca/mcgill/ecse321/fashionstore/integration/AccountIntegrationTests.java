package ca.mcgill.ecse321.fashionstore.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.fashionstore.dto.AccountListResponseDto;
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
import org.junit.jupiter.api.Order;
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
class AccountIntegrationTests {
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
    private static final String errorLoc = "$.errors";

    // Error messages
    private static final String responseNullError = "Response body is null";

    // Emails
    private static final String EMPLOYEE_EMAIL = "employee@fashionstore.com";
    private static final String CUSTOMER_EMAIL = "customer@fashionstore.com";
    private static final String NEW_EMPLOYEE_EMAIL = "newemployee@fashionstore.com";
    private static final String NEW_CUSTOMER_EMAIL = "newcustomer@fashionstore.com";

    /** Setup for accountService integration tests. */
    @BeforeAll
    void setup() {
        employeeRepository.deleteAll();
        customerRepository.deleteAll();

        // initialize
        Owner owner = new Owner();
        owner.setEmail("owner@fashionstore.com");
        owner.setPassword("owner123");
        this.owner = owner;
        Employee employee = new Employee();
        employee.setEmail(EMPLOYEE_EMAIL);
        employee.setPassword("employee123");
        this.employee = employee;
        Customer customer = new Customer();
        customer.setEmail(CUSTOMER_EMAIL);
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
    @Order(0)
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
    @Order(0)
    void accountEmployeeLogin() {
        // Arrange
        AccountRequestDto accountRequestDto = new AccountRequestDto(EMPLOYEE_EMAIL, "employee123");

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
    @Order(0)
    void accountCustomerLogin() {
        // Arrange
        AccountRequestDto accountRequestDto = new AccountRequestDto(CUSTOMER_EMAIL, "customer123");

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
    @Order(0)
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
    @Order(0)
    void failAccountCustomerLogin() {
        // Arrange
        AccountRequestDto accountRequestDto = new AccountRequestDto(CUSTOMER_EMAIL, "customer124");

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
    @Order(0)
    void failAccountEmployeeLogin() {
        // Arrange
        AccountRequestDto accountRequestDto = new AccountRequestDto(EMPLOYEE_EMAIL, "employee1234");

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
    @Order(1)
    void createEmployeeAccount() {
        AccountRequestDto accountRequestDto = new AccountRequestDto(NEW_EMPLOYEE_EMAIL, "worker01");
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

        assertNotNull(response, responseNullError);
        assertNotNull(response.id(), "Created employee account response has null ID.");
        assertEquals(NEW_EMPLOYEE_EMAIL, response.email(), "Wrong email.");
        assertEquals(
                AccountResponseDto.AccountType.EMPLOYEE,
                response.accountType(),
                "Wrong account type.");
    }

    /**
     * Test unsuccessful employee account creation when duplicated email.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    @Order(2)
    void failCreateEmployeeAccountDuplicateEmail() {
        // Arrange
        AccountRequestDto accountRequestDto = new AccountRequestDto(EMPLOYEE_EMAIL, "worker01");

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
        assertNull(
                response.accountType(),
                "Duplicate employee account creation should have null account type.");
    }

    /**
     * Test successful customer account creation.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    @Order(1)
    void createCustomerAccount() {
        AccountRequestDto accountRequestDto = new AccountRequestDto(NEW_CUSTOMER_EMAIL, "client01");
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

        assertNotNull(response, responseNullError);
        assertNotNull(response.id(), "Created customer account response has null ID.");
        assertEquals(NEW_CUSTOMER_EMAIL, response.email(), "Wrong email.");
        assertEquals(
                AccountResponseDto.AccountType.CUSTOMER,
                response.accountType(),
                "Wrong account type.");
    }

    /**
     * Test unsuccessful customer account creation when duplicated email.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    @Order(2)
    void failCreateCustomerAccountDuplicateEmail() {
        // Arrange
        AccountRequestDto accountRequestDto = new AccountRequestDto(CUSTOMER_EMAIL, "client01");

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
        assertNull(
                response.accountType(),
                "Duplicate customer account creation should have null account type.");
    }

    /**
     * Integration test to get all accounts
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(3)
    void getAllAccounts() {
        // Act
        AccountListResponseDto response =
                client.get()
                        .uri("/fashionstore/account")
                        .exchange()
                        .expectStatus()
                        .isOk()
                        .expectBody(AccountListResponseDto.class)
                        .returnResult()
                        .getResponseBody();

        // Assert
        assertNotNull(response, responseNullError);
        assertEquals(2, response.owners().size(), "Invalid number of owners.");
        assertEquals(2, response.customers().size(), "Invalid number of customers.");
        assertEquals(2, response.employees().size(), "Invalid number of employees.");
    }

    /**
     * Integration test to delete an owner (expected to fail).
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(4)
    void failDeleteOwnerAccount() {
        // Act
        client.delete()
                .uri("/fashionstore/account/{accountId}", owner.getId())
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath(errorLoc)
                .isEqualTo("Cannot delete owner account.");
    }

    /**
     * Integration test to delete a customer.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(4)
    void deleteCustomerAccount() {
        // Act
        client.delete()
                .uri("/fashionstore/account/{accountId}", customer.getId())
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();
    }

    /**
     * Integration test to delete an employee.
     *
     * @author Cyrus Fung (cfung89)
     */
    @Test
    @Order(4)
    void deleteEmployeeAccount() {
        // Act
        client.delete()
                .uri("/fashionstore/account/{accountId}", employee.getId())
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody()
                .isEmpty();
    }
}
