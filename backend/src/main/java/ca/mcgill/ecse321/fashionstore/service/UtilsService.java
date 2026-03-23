package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.OrderItem;
import ca.mcgill.ecse321.fashionstore.model.ShoppingCartItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem.Colour;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem.Size;
import ca.mcgill.ecse321.fashionstore.model.Order.State;
import ca.mcgill.ecse321.fashionstore.repository.AccountRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderRepository;
import ca.mcgill.ecse321.fashionstore.repository.ShoppingCartItemRepository;

import java.sql.Date;
import java.util.Random;

import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Utility service class to create test data and delete all data from the
 * database.
 */
@Service
@Validated
@Profile("dev")
public class UtilsService {
    private final ClothingItemRepository clothingItemRepository;
    private final ClothingProductRepository clothingProductRepository;
    private final CustomerRepository customerRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final Random random;

    /**
     * UtilsService constructor.
     *
     * @param clothingItemRepository    ClothingItemRepository required to access
     *                                  the database.
     * @param clothingProductRepository ClothingProductRepository required to access
     *                                  the database.
     * @param customerRepository        CustomerRepository required to access the
     *                                  database.
     * @param employeeRepository        EmployeeRepository required to access the
     *                                  database.
     * @param orderItemRepository       OrderItemRepository required to access the
     *                                  database.
     * @param orderRepository           OrderRepository required to access the
     *                                  database.
     * @author Cyrus Fung
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public UtilsService(
            ClothingItemRepository clothingItemRepository,
            ClothingProductRepository clothingProductRepository,
            CustomerRepository customerRepository,
            EmployeeRepository employeeRepository,
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository,
            ShoppingCartItemRepository shoppingCartItemRepository) {
        this.clothingItemRepository = clothingItemRepository;
        this.clothingProductRepository = clothingProductRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.random = new Random();
            }

    /**
     * Generate random data.
     *
     * @author Cyrus Fung
     */
    public void generateRandomData() {
    }

    /**
     * Generate predefined data.
     *
     * @author Cyrus Fung
     */
    public void generateData() {
        Customer customer0 = createCustomer("ada@language.com", "19801980", "0 Language Avenue", 0);
        Customer customer1 = createCustomer("basic@language.com", "19641964", "1 Language Avenue", 100);
        Customer customer2 = createCustomer("c@language.com", "19731973", "2 Language Avenue", 200);
        Employee employee0 = createEmployee("dart@language.com", "20112011", "3 Language Avenue", 300);
        Employee employee1 = createEmployee("erlang@language.com", "19861986", "4 Language Avenue", 400);
        Employee employee2 = createEmployee("fsharp@language.com", "20052005", "5 Language Avenue", 500);
        ClothingProduct clothingProduct0 = createClothingProduct("Go", 20.0f, null);
        ClothingProduct clothingProduct1 = createClothingProduct("Haskell", 40.0f, null);
        ClothingProduct clothingProduct2 = createClothingProduct("Jai", 60.0f, null);
        ClothingItem clothingItem0 = createClothingItem(Size.XS, Colour.BLACK, 0);
        ClothingItem clothingItem1 = createClothingItem(Size.S, Colour.BLUE, 10);
        ClothingItem clothingItem2 = createClothingItem(Size.M, Colour.BROWN, 20);
        ClothingItem clothingItem3 = createClothingItem(Size.L, Colour.GREEN, 30);
        ClothingItem clothingItem4 = createClothingItem(Size.XL, Colour.PINK, 40);
        Order order0 = createOrder(State.PURCHASED, 20f, null, null,"0 Language Avenue");
        Order order1 = createOrder(State.ASSIGNED, 60f, null, null,"1 Language Avenue");
        Order order2 = createOrder(State.PREPARED, 120f, null, null,"2 Language Avenue");
        Order order3 = createOrder(State.CANCELLED, 100f, null, null,"3 Language Avenue");
        Order order4 = createOrder(State.DELIVERED, 120f, null, null,"4 Language Avenue");
        OrderItem orderItem0 = createOrderItem(2, 20f);
        OrderItem orderItem1 = createOrderItem(4, 40f);
        OrderItem orderItem2 = createOrderItem(6, 60f);
        ShoppingCartItem shoppingCartItem0 = createShoppingCartItem(2);
        ShoppingCartItem shoppingCartItem1 = createShoppingCartItem(4);
        ShoppingCartItem shoppingCartItem2 = createShoppingCartItem(6);
    }

    /**
     * Delete all data from database
     *
     * @author Cyrus Fung
     */
    public void deleteAllData() {
        clothingItemRepository.deleteAll();
        clothingProductRepository.deleteAll();
        customerRepository.deleteAll();
        employeeRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        shoppingCartItemRepository.deleteAll();
    }

    private String generateString(int length) {
        StringBuilder buffer = new StringBuilder(length);
        int num = 0;
        for (int i = 0; i < length; i++) {
            num = random.nextInt(52) + 65;
            if (num >= 91) {
                num += 6;
            }
            buffer.append((char) num);
        }
        return buffer.toString();
    }

    private ClothingItem createClothingItem(Size size, Colour colour, int numInStock) {
        ClothingItem clothingItem = new ClothingItem();
        clothingItem.setSize(size);
        clothingItem.setColour(colour);
        clothingItem.setNumInStock(numInStock);
        return clothingItemRepository.save(clothingItem);
    }

    private ClothingProduct createClothingProduct(String name, float price, String image) {
        ClothingProduct clothingProduct = new ClothingProduct();
        clothingProduct.setName(name);
        clothingProduct.setPrice(price);
        clothingProduct.setImage(image);
        return clothingProductRepository.save(clothingProduct);
    }

    private Customer createCustomer(String email, String password, String address, int numLoyaltyPoints) {
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setAddress(address);
        customer.setNumLoyaltyPoints(numLoyaltyPoints);
        return customerRepository.save(customer);
    }

    private Employee createEmployee(String email, String password, String address, int numLoyaltyPoints) {
        Employee employee = new Employee();
        employee.setEmail(email);
        employee.setPassword(password);
        employee.setAddress(address);
        employee.setNumLoyaltyPoints(numLoyaltyPoints);
        return employeeRepository.save(employee);
    }

    private OrderItem createOrderItem(int quantity, float purchasePrice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(quantity);
        orderItem.setPurchasePrice(purchasePrice);
        return orderItemRepository.save(orderItem);
    }

    private Order createOrder(State state, float price, Date orderDate, Date deliveryDate, String deliveryAddress) {
        Order order = new Order();
        order.setState(state);
        order.setPrice(price);
        order.setOrderDate(orderDate);
        order.setDeliveryDate(deliveryDate);
        order.setDeliveryAddress(deliveryAddress);
        return orderRepository.save(order);
    }

    private ShoppingCartItem createShoppingCartItem(int quantity) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setQuantity(quantity);
        return shoppingCartItemRepository.save(shoppingCartItem);
    }
}
