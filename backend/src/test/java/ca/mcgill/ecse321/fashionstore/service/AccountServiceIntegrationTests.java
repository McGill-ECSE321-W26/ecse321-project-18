package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.mcgill.ecse321.fashionstore.controller.AccountController;
import ca.mcgill.ecse321.fashionstore.dto.AccountRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.AccountResponseDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
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
import org.springframework.boot.test.context.SpringBootTest;

/** Account Service class integration tests. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountServiceIntegrationTests {
    @Autowired private AccountController accountController;

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private CustomerRepository customerRepository;

    /** Setup for accountService integration tests. */
    @BeforeAll
    void setup() {
        // Dummy owner
        Owner newOwner = new Owner();
        newOwner.setEmail("owner@fashionstore.com");
        newOwner.setPassword("owner123");
        ownerRepository.save(newOwner);
    }

    /** Clear database after all tests */
    @AfterAll
    void clearDatabase() {
        ownerRepository.deleteAll();
        employeeRepository.deleteAll();
        customerRepository.deleteAll();
    }

    /**
     * Test successful creation of an employee account.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    @Order(1)
    void successCreationEmployeeAccount() {
        String email = "employee@fashionstore.com";
        String password = "employee123";
        AccountRequestDto req = new AccountRequestDto(email, password);
        AccountResponseDto response =
                assertDoesNotThrow(
                        () -> accountController.createEmployeeAccount(req),
                        String.format(
                                "Account creation failed with email: %s and password %s.",
                                email, password));

        // Simple check in employee repository
        Employee employee = employeeRepository.findEmployeeById(response.id());
        assertEquals(
                response.email(),
                employee.getEmail(),
                "Created account and saved account email do not match.");
    }

    /**
     * Test employee login fail due to password.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    @Order(2)
    void badEmployeePasswordAccountLogin() {
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("employee@fashionstore.com", "employee1233");
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> accountController.accountLogin(accountRequestDto),
                        "Login passed with wrong credentials.");
        assertEquals(
                AccountService.badPasswordErrorMsg,
                e.getMessage(),
                String.format(
                        """
                        Login failure error message is wrong.%n\
                        Current: %s%n\
                        Should be: %s%n\
                        """,
                        e.getMessage(), AccountService.badPasswordErrorMsg));
    }

    /**
     * Test employee login fail due to email. Password is correct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    @Order(3)
    void badEmployeeEmailAccountLogin() {
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("employe@fashionstore.com", "employee123");
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> accountController.accountLogin(accountRequestDto),
                        "Login passed with wrong credentials.");
        assertEquals(
                AccountService.nonexistentEmailErrorMsg,
                e.getMessage(),
                String.format(
                        """
                        Login failure error message is wrong.%n\
                        Current: %s%n\
                        Should be: %s%n\
                        """,
                        e.getMessage(), AccountService.nonexistentEmailErrorMsg));
    }
}
