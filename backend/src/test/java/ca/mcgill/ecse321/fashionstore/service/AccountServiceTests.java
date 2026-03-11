package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.mcgill.ecse321.fashionstore.controller.AccountController;
import ca.mcgill.ecse321.fashionstore.dto.AccountRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.AccountResponseDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.model.Owner;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OwnerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test suite for AccountService methods.
 *
 * @author Qiuyu Huang (redacted24)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountServiceTests {
    @Autowired private AccountController accountController;
    @Autowired private AccountService accountService;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private CustomerRepository customerRepository;

    private Owner owner;
    private Employee employee;
    private Customer customer;

    // Error messages
    private static final String badEmailError =
            """
            Response wrong email.%n\
            Expected: %s%n\
            Actual: %s%n\
            """;
    private static final String wrongLoginFailureErrorMsg =
            """
            Login failure error message is wrong.%n\
            Current: %s%n\
            Should be: %s%n\
            """;
    private static final String badCredentialsLoginSuccessErrorMsg =
            "Login passed with wrong credentials.";

    @BeforeEach
    void loadDatabase() {
        // Dummy employee
        Employee newEmployee = new Employee();
        newEmployee.setEmail("employee@fashionstore.com");
        newEmployee.setPassword("employee123");
        newEmployee.setAddress("1000 Peel Street");
        employeeRepository.save(newEmployee);
        this.employee = newEmployee;

        // Dummy owner
        Owner newOwner = new Owner();
        newOwner.setEmail("owner@fashionstore.com");
        newOwner.setPassword("owner123");
        ownerRepository.save(newOwner);
        this.owner = newOwner;

        // Dummy customer
        Customer newCustomer = new Customer();
        newCustomer.setEmail("customer@fashionstore.com");
        newCustomer.setAddress("1001 Customer Street");
        newCustomer.setPassword("customer123");
        customerRepository.save(newCustomer);
        this.customer = newCustomer;
    }

    @AfterEach
    void clearDatabase() {
        // Clear entities
        employeeRepository.deleteAll();
        ownerRepository.deleteAll();
        customerRepository.deleteAll();
    }

    /**
     * Test login with nonexistent email.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void accountLoginBadEmail() {
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("nonexistent@fashionstore.com", "owner123");
        assertThrows(
                FashionStoreException.class,
                () -> accountService.accountLoginCheck(accountRequestDto),
                "Login with nonexistent email should fail.");
    }

    /**
     * Test login with wrong password.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void accountLoginBadPassword() {
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("owner@fashionstore.com", "owner789");
        assertThrows(
                FashionStoreException.class,
                () -> accountService.accountLoginCheck(accountRequestDto),
                "Login with correct email but bad password should fail.");
    }

    /**
     * Test findAccountType method: find for owner
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void findAccountTypeOwner() {
        int id = owner.getId();
        assertEquals(
                AccountResponseDto.AccountType.OWNER,
                accountService.findAccountType(id),
                "Account type retrieval is wrong for owner.");
    }

    /**
     * Test findAccountType method: find for employee
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void findAccountTypeEmployee() {
        int id = employee.getId();
        assertEquals(
                AccountResponseDto.AccountType.EMPLOYEE,
                accountService.findAccountType(id),
                "Account type retrieval is wrong for employee.");
    }

    /**
     * Test findAccountType method: find for customer
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void findAccountTypeCustomer() {
        int id = customer.getId();
        assertEquals(
                AccountResponseDto.AccountType.CUSTOMER,
                accountService.findAccountType(id),
                "Account type retrieval is wrong for customer.");
    }

    /**
     * Test employee login fail due to password. Email is correct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void badEmployeePasswordAccountLogin() {
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("employee@fashionstore.com", "employee1233");
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> accountController.accountLogin(accountRequestDto),
                        badCredentialsLoginSuccessErrorMsg);
        assertEquals(
                AccountService.badPasswordErrorMsg,
                e.getMessage(),
                String.format(
                        wrongLoginFailureErrorMsg,
                        e.getMessage(),
                        AccountService.badPasswordErrorMsg));
    }

    /**
     * Test employee login fail due to email. Password is correct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void badEmployeeEmailAccountLogin() {
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("employe@fashionstore.com", "employee123");
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> accountController.accountLogin(accountRequestDto),
                        badCredentialsLoginSuccessErrorMsg);
        assertEquals(
                AccountService.nonexistentEmailErrorMsg,
                e.getMessage(),
                String.format(
                        wrongLoginFailureErrorMsg,
                        e.getMessage(),
                        AccountService.nonexistentEmailErrorMsg));
    }

    /**
     * Test employee login success.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void successEmployeeAccountLogin() {
        AccountRequestDto accountRequestDto =
                new AccountRequestDto(employee.getEmail(), employee.getPassword());
        AccountResponseDto response =
                assertDoesNotThrow(
                        () -> accountController.accountLogin(accountRequestDto),
                        "Login with correct credentials does not pass.");
        assertEquals(
                employee.getEmail(),
                response.email(),
                String.format(badEmailError, employee.getEmail(), response.email()));
    }

    /**
     * Test customer login fail due to password. Email is correct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void badCustomerPasswordAccountLogin() {
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("customer@fashionstore.com", "customer1233");
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> accountController.accountLogin(accountRequestDto),
                        badCredentialsLoginSuccessErrorMsg);
        assertEquals(
                AccountService.badPasswordErrorMsg,
                e.getMessage(),
                String.format(
                        wrongLoginFailureErrorMsg,
                        e.getMessage(),
                        AccountService.badPasswordErrorMsg));
    }

    /**
     * Test customer login fail due to email. Password is correct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void badCustomerEmailAccountLogin() {
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("employe@fashionstore.com", "customer123");
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> accountController.accountLogin(accountRequestDto),
                        badCredentialsLoginSuccessErrorMsg);
        assertEquals(
                AccountService.nonexistentEmailErrorMsg,
                e.getMessage(),
                String.format(
                        wrongLoginFailureErrorMsg,
                        e.getMessage(),
                        AccountService.nonexistentEmailErrorMsg));
    }

    /**
     * Test customer login success.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void successCustomerAccountLogin() {
        AccountRequestDto accountRequestDto =
                new AccountRequestDto(customer.getEmail(), customer.getPassword());
        AccountResponseDto response =
                assertDoesNotThrow(
                        () -> accountController.accountLogin(accountRequestDto),
                        "Login with correct credentials does not pass.");
        assertEquals(
                customer.getEmail(),
                response.email(),
                String.format(badEmailError, customer.getEmail(), response.email()));
    }
}
