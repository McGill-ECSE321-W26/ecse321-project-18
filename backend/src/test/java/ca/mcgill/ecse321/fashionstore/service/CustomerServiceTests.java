package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.fashionstore.dto.AccountRequestDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.repository.AccountRepository;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

/** Customer Service class tests. */
@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class CustomerServiceTests {

    @Mock private CustomerRepository customerRepository;
    @Mock private AccountRepository accountRepository;

    @InjectMocks CustomerService customerService;

    private static final String VALID_EMAIL = "richcustomer@fashionstore.com";
    private static final String VALID_PASSWORD = "pa55w0rd";
    private static final int CUSTOMER_ID = 1;

    /**
     * Service layer test for creating a customer with a valid email and password.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testCreateCustomerValid() {
        // Arrange
        when(accountRepository.existsByEmail(VALID_EMAIL)).thenReturn(false);
        when(customerRepository.save(any(Customer.class)))
                .thenAnswer((InvocationOnMock inv) -> {
                    Customer c = (Customer) inv.getArgument(0);
                    c.setId(CUSTOMER_ID);
                    return c;
                });

        AccountRequestDto dto = new AccountRequestDto(VALID_EMAIL, VALID_PASSWORD);

        // Act
        Customer result = customerService.createCustomer(dto);

        // Assert
        assertNotNull(result, "Created customer should not be null.");
        assertEquals(VALID_EMAIL, result.getEmail(), "Customer should have the correct email.");
        assertEquals(VALID_PASSWORD, result.getPassword(), "Customer should have the correct password.");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    /**
     * Service layer test for creating a customer with a duplicate email.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void testCreateCustomerDuplicateEmail() {
        // Arrange
        when(accountRepository.existsByEmail(VALID_EMAIL)).thenReturn(true);

        AccountRequestDto dto = new AccountRequestDto(VALID_EMAIL, VALID_PASSWORD);

        // Assert
        FashionStoreException e =
                assertThrows(
                        FashionStoreException.class,
                        () -> customerService.createCustomer(dto));

        assertEquals(
                HttpStatus.CONFLICT,
                e.getStatus(),
                "HTTP status should be CONFLICT when email already exists.");
        assertEquals(
                String.format("Account with email %s already exists.", VALID_EMAIL),
                e.getMessage(),
                "Error message should indicate the duplicate email.");
    }
}