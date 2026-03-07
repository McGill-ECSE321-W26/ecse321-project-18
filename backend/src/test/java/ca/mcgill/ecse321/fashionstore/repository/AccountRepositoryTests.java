package ca.mcgill.ecse321.fashionstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.fashionstore.model.Account;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.model.Owner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test suite for account persistence in the database.
 *
 * @author Flavie Qin (flavieq88)
 */
@SpringBootTest
class AccountRepositoryTests {
    @Autowired private AccountRepository accountRepository;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private CustomerRepository customerRepository;
    private Owner owner;
    private Employee employee;
    private Customer customer;

    /**
     * Creates and saves an owner, an employee and a customer before each test.
     *
     * @author Flavie Qin (flavieq88)
     */
    @BeforeEach
    void createAndSaveAccounts() {
        // create and save Owner object
        Owner newOwner = new Owner();
        newOwner.setEmail("owner@fashionstore.com");
        newOwner.setPassword("averysafepassword");
        accountRepository.save(newOwner);
        this.owner = newOwner;

        // create and save Employee object
        Employee newEmployee = new Employee();
        newEmployee.setEmail("employee@fashionstore.com");
        newEmployee.setPassword("thisismypassword");
        accountRepository.save(newEmployee);
        this.employee = newEmployee;

        // create and save Owner object
        Customer newCustomer = new Customer();
        newCustomer.setEmail("customer@fashionstore.com");
        newCustomer.setPassword("customerpassword");
        accountRepository.save(newCustomer);
        this.customer = newCustomer;
    }

    /**
     * Clears database after each test.
     *
     * @author Flavie Qin (flavieq88)
     */
    @AfterEach
    void clearDatabase() {
        accountRepository.deleteAll();
        ownerRepository.deleteAll();
        employeeRepository.deleteAll();
        customerRepository.deleteAll();
    }

    /**
     * Test retrieval of persisted accounts from database: should not be null.
     *
     * @author Flavie Qin (flavieq88)
     */
    @Test
    void testPersistAndLoadAccounts() {
        // read all accounts from database
        Account ownerFromDb = accountRepository.findAccountById(owner.getId());
        Account employeeFromDb = accountRepository.findAccountById(employee.getId());
        Account customerFromDb = accountRepository.findAccountById(customer.getId());

        // assert that accounts were found
        assertNotNull(ownerFromDb, "Could not find saved owner in the database");
        assertNotNull(employeeFromDb, "Could not find saved employee in the database");
        assertNotNull(customerFromDb, "Could not find saved customer in the database");
    }

    /**
     * Test retrieval of persisted accounts from database using the subclass repositories: should
     * not be null.
     *
     * @author Flavie Qin (flavieq88)
     */
    @Test
    void testPersistAndLoadAccountsSubclasses() {
        // read owner from database using owner repository
        Owner ownerFromDb = ownerRepository.findOwnerById(owner.getId());
        // read employee from database using employee repository
        Employee employeeFromDb = employeeRepository.findEmployeeById(employee.getId());
        // read customer from database using customer repository
        Customer customerFromDb = customerRepository.findCustomerById(customer.getId());

        // assert that owner was found
        assertNotNull(ownerFromDb, "Could not find saved owner in the database");
        // assert that employee was found
        assertNotNull(employeeFromDb, "Could not find saved employee in the database");
        // assert that customer was found
        assertNotNull(customerFromDb, "Could not find saved customer in the database");
    }

    /**
     * Test read (retrieval) of account email and passwords.
     *
     * @author Flavie Qin (flavieq88)
     */
    @Test
    void testReadAccountData() {
        // get an account from database
        Account ownerFromDb = accountRepository.findAccountById(owner.getId());

        // check that id was persisted
        assertEquals(owner.getId(), ownerFromDb.getId(), "Account email is not saved in database");
        // check that email was persisted
        assertEquals(
                owner.getEmail(), ownerFromDb.getEmail(), "Account email is not saved in database");
        // check that password was persisted
        assertEquals(
                owner.getPassword(),
                ownerFromDb.getPassword(),
                "Account password is not saved in database");
    }

    /**
     * Test write of account password. Note that email write is not tested because email is the
     * primary key, making it a unique and immutable attribute.
     *
     * @author Flavie Qin (flavieq88)
     */
    @Test
    void testWriteOwnerPassword() {
        // get owner from database
        Account employeeFromDb = accountRepository.findAccountById(employee.getId());

        // update the password
        String newPassword = "differentemployeepassword";
        employeeFromDb.setPassword(newPassword);
        accountRepository.save(employeeFromDb);

        // check that new password was persisted
        Account updatedEmployeeFromDb = accountRepository.findAccountById(employee.getId());
        assertEquals(
                newPassword,
                updatedEmployeeFromDb.getPassword(),
                "Account password update was not persisted");

        // check that new password was persisted in the employee table too
        Employee updatedEmployeeFromDbSubclass =
                employeeRepository.findEmployeeById(employee.getId());
        assertEquals(
                newPassword,
                updatedEmployeeFromDbSubclass.getPassword(),
                "Employee password update was not persisted");
    }

    /**
     * Test deleting an account.
     *
     * @author Flavie Qin (flavieq88)
     */
    @Test
    void testDeleteAccount() {
        // delete the account from the database
        accountRepository.delete(customer);

        // check that we can no longer find an account with that email
        Account accountFromDb = accountRepository.findAccountById(customer.getId());
        assertNull(accountFromDb, "Account deletion was not persisted");

        // also check that employee account was deleted from employee repository
        Customer customerFromDb = customerRepository.findCustomerById(customer.getId());
        assertNull(customerFromDb, "Customer deletion was not persisted");
    }

    /**
     * Test deleting an from the subclass repository.
     *
     * @author Flavie Qin (flavieq88)
     */
    @Test
    void testDeleteAccountFromSubclass() {
        // delete the account from the database using the owner repository
        ownerRepository.delete(owner);

        // check that we can no longer find an account with that email
        Owner ownerFromDb = ownerRepository.findOwnerById(owner.getId());
        assertNull(ownerFromDb, "Owner deletion was not persisted");

        // also check that employee account was deleted from employee repository
        Account accountFromDb = accountRepository.findAccountById(owner.getId());
        assertNull(accountFromDb, "Account deletion was not persisted");
    }

    /**
     * Test account retrieval by email.
     *
     * @author Qiuyu Huang (redacted24)
     */
    @Test
    void testAccountFindByEmail() {
        assertNotNull(accountRepository.findAccountByEmail("owner@fashionstore.com"));
    }
}
