package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test suite for AccountService methods.
 *
 * @author Qiuyu Huang (redacted24)
 */
@SpringBootTest
class AccountServiceTests {
    @Autowired private AccountService accountService;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private CustomerRepository customerRepository;

    private Owner owner;
    private Employee employee;
    private Customer customer;

    @BeforeEach
    void loadDatabase() {
        // Dummy employee
        Employee newEmployee = new Employee();
        newEmployee.setEmail("newEmployee@fashionstore.com");
        newEmployee.setPassword("newEmployee123");
        newEmployee.setAddress("1000 Peel Street");
        employeeRepository.save(newEmployee);
        this.employee = newEmployee;

        // Dummy owner
        Owner newOwner = new Owner();
        newOwner.setEmail("newOwner@fashionstore.com");
        newOwner.setPassword("newOwner123");
        ownerRepository.save(newOwner);
        this.owner = newOwner;

        // Dummy customer
        Customer newCustomer = new Customer();
        newCustomer.setEmail("newCustomer@fashionstore.com");
        newCustomer.setAddress("1001 Customer Street");
        newCustomer.setPassword("newCustomer123");
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
}
