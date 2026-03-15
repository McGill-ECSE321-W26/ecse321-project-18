package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.fashionstore.dto.AccountRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.AccountResponseDto.AccountType;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Account;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.model.Owner;
import ca.mcgill.ecse321.fashionstore.repository.AccountRepository;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

/**
 * Test suite for AccountService methods.
 *
 * @author Qiuyu Huang (redacted24)
 */
@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class AccountServiceTests {
    @Mock private AccountRepository accountRepository;

    @Mock private EmployeeRepository employeeRepository;
    @Mock private OwnerRepository ownerRepository;
    @Mock private CustomerRepository customerRepository;

    @InjectMocks private AccountService accountService;

    private Owner owner;
    private Employee employee;
    private Customer customer;

    // Variables
    private static final String badEmail = "employe@fashionstore.com";

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

    /**
     * Setup function for all tests. Creates a mock employee, owner and customer.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @BeforeEach
    void loadDatabase() {
        this.employee = createEmployee();
        this.owner = createOwner();
        this.customer = createCustomer();
    }

    /**
     * Helper function for initializing dummy employee.
     *
     * @return A mock Employee.
     * @author Qiuyu Huang (redacted24)
     */
    @Disabled("Helper function")
    Employee createEmployee() {
        // Dummy employee
        Employee newEmployee = new Employee();
        newEmployee.setEmail("employee@fashionstore.com");
        newEmployee.setPassword("employee123");
        newEmployee.setAddress("1000 Peel Street");
        return newEmployee;
    }

    /**
     * Helper function for initializing dummy owner.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Disabled("Helper function")
    Owner createOwner() {
        // Dummy owner
        Owner newOwner = new Owner();
        newOwner.setEmail("owner@fashionstore.com");
        newOwner.setPassword("owner123");
        return newOwner;
    }

    /**
     * Helper function for initializing dummy customer.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Disabled("Helper function")
    Customer createCustomer() {
        // Dummy customer
        Customer newCustomer = new Customer();
        newCustomer.setEmail("customer@fashionstore.com");
        newCustomer.setAddress("1001 Customer Street");
        newCustomer.setPassword("customer123");
        return newCustomer;
    }

    /**
     * Test login with nonexistent email.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void accountLoginBadEmail() {
        // Arrange
        FashionStoreException exception =
                new FashionStoreException(
                        HttpStatus.BAD_REQUEST, AccountService.nonexistentEmailErrorMsg);
        String badEmail = "nonexistent@fashionstore.com";
        AccountRequestDto accountRequestDto = new AccountRequestDto(badEmail, "owner123");
        when(accountRepository.findAccountByEmail(accountRequestDto.email())).thenThrow(exception);

        // Act
        // Assert
        assertThrows(
                FashionStoreException.class,
                () -> accountService.accountLoginCheck(accountRequestDto),
                "Login with nonexistent email should fail.");
        verify(accountRepository, times(1)).findAccountByEmail(accountRequestDto.email());
    }

    /**
     * Test login with wrong password.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void accountLoginBadPassword() {
        // Arrange
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("owner@fashionstore.com", "owner789");
        when(accountRepository.findAccountByEmail(accountRequestDto.email())).thenReturn(owner);

        // Act
        // Assert
        assertThrows(
                FashionStoreException.class,
                () -> accountService.accountLoginCheck(accountRequestDto),
                "Login with correct email but bad password should fail.");
        verify(accountRepository, times(1)).findAccountByEmail(accountRequestDto.email());
    }

    /**
     * Test findAccountType method: find for owner
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void findAccountTypeOwner() {
        // Arrange
        int id = owner.getId();
        when(ownerRepository.findOwnerById(id)).thenReturn(owner);

        // Act
        // Assert
        assertEquals(
                AccountType.OWNER,
                accountService.findAccountType(id),
                "Account type retrieval is wrong for owner.");
        verify(ownerRepository, times(1)).findOwnerById(id);
    }

    /**
     * Test findAccountType method: find for employee
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void findAccountTypeEmployee() {
        // Arrange
        int id = employee.getId();
        when(ownerRepository.findOwnerById(id)).thenReturn(null);
        when(employeeRepository.findEmployeeById(id)).thenReturn(employee);

        // Act
        // Assert
        assertEquals(
                AccountType.EMPLOYEE,
                accountService.findAccountType(id),
                "Account type retrieval is wrong for employee.");
        verify(ownerRepository, times(1)).findOwnerById(id);
        verify(employeeRepository, times(1)).findEmployeeById(id);
    }

    /**
     * Test findAccountType method: find for customer
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void findAccountTypeCustomer() {
        // Arrange
        int id = customer.getId();
        when(ownerRepository.findOwnerById(id)).thenReturn(null);
        when(employeeRepository.findEmployeeById(id)).thenReturn(null);
        when(customerRepository.findCustomerById(id)).thenReturn(customer);

        // Act
        // Assert
        assertEquals(
                AccountType.CUSTOMER,
                accountService.findAccountType(id),
                "Account type retrieval is wrong for customer.");
        verify(ownerRepository, times(1)).findOwnerById(id);
        verify(employeeRepository, times(1)).findEmployeeById(id);
    }

    /**
     * Test employee login fail due to password. Email is correct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void badEmployeePasswordAccountLogin() {
        // Arrange
        AccountRequestDto accountRequestDto =
                new AccountRequestDto("employee@fashionstore.com", "employee1233");
        when(accountRepository.findAccountByEmail(employee.getEmail())).thenReturn(employee);

        // Act
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> accountService.accountLoginCheck(accountRequestDto),
                        badCredentialsLoginSuccessErrorMsg);

        // Assert
        assertEquals(
                AccountService.badPasswordErrorMsg,
                e.getMessage(),
                String.format(
                        wrongLoginFailureErrorMsg,
                        e.getMessage(),
                        AccountService.badPasswordErrorMsg));
        verify(accountRepository, times(1)).findAccountByEmail(employee.getEmail());
    }

    /**
     * Test employee login fail due to email. Password is correct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void badEmployeeEmailAccountLogin() {
        // Arrange
        FashionStoreException exception =
                new FashionStoreException(
                        HttpStatus.BAD_REQUEST, AccountService.nonexistentEmailErrorMsg);
        AccountRequestDto accountRequestDto = new AccountRequestDto(badEmail, "employee123");
        when(accountRepository.findAccountByEmail(badEmail)).thenThrow(exception);

        // Act
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> accountService.accountLoginCheck(accountRequestDto),
                        badCredentialsLoginSuccessErrorMsg);

        // Assert
        assertEquals(
                AccountService.nonexistentEmailErrorMsg,
                e.getMessage(),
                String.format(
                        wrongLoginFailureErrorMsg,
                        e.getMessage(),
                        AccountService.nonexistentEmailErrorMsg));
        verify(accountRepository, times(1)).findAccountByEmail(badEmail);
    }

    /**
     * Test customer login fail due to password. Email is correct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void badCustomerPasswordAccountLogin() {
        // Arrange
        AccountRequestDto accountRequestDto =
                new AccountRequestDto(customer.getEmail(), "randompassword123");
        when(accountRepository.findAccountByEmail(customer.getEmail())).thenReturn(customer);

        // Act
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> accountService.accountLoginCheck(accountRequestDto),
                        badCredentialsLoginSuccessErrorMsg);

        // Assert
        assertEquals(
                AccountService.badPasswordErrorMsg,
                e.getMessage(),
                String.format(
                        wrongLoginFailureErrorMsg,
                        e.getMessage(),
                        AccountService.badPasswordErrorMsg));
        verify(accountRepository, times(1)).findAccountByEmail(customer.getEmail());
    }

    /**
     * Test customer login fail due to email. Password is correct.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void badCustomerEmailAccountLogin() {
        // Arrange
        // Create exception to be thrown
        FashionStoreException toThrow =
                new FashionStoreException(
                        HttpStatus.BAD_REQUEST, AccountService.nonexistentEmailErrorMsg);
        AccountRequestDto accountRequestDto = new AccountRequestDto(badEmail, "customer123");
        when(accountRepository.findAccountByEmail(badEmail)).thenThrow(toThrow);

        // Act
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> accountService.accountLoginCheck(accountRequestDto),
                        badCredentialsLoginSuccessErrorMsg);

        // Assert
        assertEquals(
                AccountService.nonexistentEmailErrorMsg,
                e.getMessage(),
                String.format(
                        wrongLoginFailureErrorMsg,
                        e.getMessage(),
                        AccountService.nonexistentEmailErrorMsg));
        verify(accountRepository, times(1)).findAccountByEmail(badEmail);
    }

    /**
     * Test customer login success.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void successCustomerAccountLogin() {
        // Arrange
        AccountRequestDto accountRequestDto =
                new AccountRequestDto(customer.getEmail(), customer.getPassword());
        when(accountRepository.findAccountByEmail(accountRequestDto.email())).thenReturn(customer);

        // Act
        // Assert
        Account response =
                assertDoesNotThrow(() -> accountService.accountLoginCheck(accountRequestDto));
        assertEquals(
                customer.getEmail(),
                response.getEmail(),
                "Customer login success response email is wrong.");
        assertEquals(
                customer.getId(), response.getId(), "Customer login success response id is wrong.");
        verify(accountRepository, times(1)).findAccountByEmail(accountRequestDto.email());
    }

    /**
     * Test employee account creation success.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void successCreateEmployeeAccount() {
        AccountRequestDto request =
                new AccountRequestDto("newemployee@fashionstore.com", "employee456");
        when(accountRepository.existsByEmail(request.email())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class)))
                .thenAnswer(invocation -> withId(invocation.getArgument(0), 101));

        Account response = assertDoesNotThrow(() -> accountService.createEmployeeAccount(request));

        assertNotNull(response, "Created employee account response should not be null.");
        assertEquals(
                request.email(),
                response.getEmail(),
                "Created employee account response has wrong email.");
        assertTrue(response instanceof Employee, "Created account should be an Employee instance.");
    }

    /**
     * Test employee account creation failure due to duplicate email.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void duplicateEmployeeEmailCreateAccount() {
        AccountRequestDto request = new AccountRequestDto(employee.getEmail(), "employee456");
        when(accountRepository.existsByEmail(request.email())).thenReturn(true);

        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> accountService.createEmployeeAccount(request),
                        "Exception for duplicate employee email.");

        assertEquals(
                String.format("An account with email %s already exists.", request.email()),
                e.getMessage(),
                "Exception message should indicate duplicate email.");
    }

    /**
     * Test customer account creation success.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void successCreateCustomerAccount() {
        AccountRequestDto request =
                new AccountRequestDto("newcustomer@fashionstore.com", "customer456");
        when(accountRepository.existsByEmail(request.email())).thenReturn(false);
        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> withId(invocation.getArgument(0), 202));

        Account response = assertDoesNotThrow(() -> accountService.createCustomerAccount(request));

        assertNotNull(response, "Created customer account response should not be null.");
        assertEquals(
                request.email(),
                response.getEmail(),
                "Created customer account response has wrong email.");
        assertTrue(response instanceof Customer, "Created account should be a Customer instance.");
    }

    /**
     * Test customer account creation failure due to duplicate email.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void duplicateCustomerEmailCreateAccount() {
        AccountRequestDto request = new AccountRequestDto(customer.getEmail(), "customer456");
        when(accountRepository.existsByEmail(request.email())).thenReturn(true);

        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> accountService.createCustomerAccount(request),
                        "Exception for duplicate customer email.");

        assertEquals(
                String.format("An account with email %s already exists.", request.email()),
                e.getMessage(),
                "Exception message should indicate duplicate email.");
    }

    /**
     * Helper method to simulate database ID assignment for mocked repository save operations. Sets
     * the given ID on the account and returns it, like auto-generated IDs in tests.
     *
     * @param <T> the type of account, must extend Account
     * @param account the account to assign the ID to
     * @param id the ID to assign to the account
     * @return the account with the assigned ID
     * @author Aurore Zhang (ororio0)
     */
    private <T extends Account> T withId(T account, int id) {
        assertTrue(account.setId(id), "Setting account ID should return true.");
        return account;
    }
}
