package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;

/** Order Service class tests. */
@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class OrderServiceTests {
    @Mock private OrderRepository orderRepository;

    @Mock private EmployeeRepository employeeRepository;

    @Mock private CustomerRepository customerRepository;

    @InjectMocks OrderService orderService;
}
