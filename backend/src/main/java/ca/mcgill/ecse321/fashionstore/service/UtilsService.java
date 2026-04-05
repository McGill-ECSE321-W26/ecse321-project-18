package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem.Colour;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem.Size;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.Order.State;
import ca.mcgill.ecse321.fashionstore.model.OrderItem;
import ca.mcgill.ecse321.fashionstore.model.ShoppingCartItem;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderRepository;
import ca.mcgill.ecse321.fashionstore.repository.ShoppingCartItemRepository;
import java.sql.Date;
import java.time.LocalDate;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/** Utility service class to create test data and delete all data from the database. */
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
    private static final Date ORDER_DATE = Date.valueOf(LocalDate.now());
    private static final Date DELIVERY_DATE = Date.valueOf(LocalDate.now().plusDays(10));

    /**
     * UtilsService constructor.
     *
     * @param clothingItemRepository ClothingItemRepository required to access the database.
     * @param clothingProductRepository ClothingProductRepository required to access the database.
     * @param customerRepository CustomerRepository required to access the database.
     * @param employeeRepository EmployeeRepository required to access the database.
     * @param orderItemRepository OrderItemRepository required to access the database.
     * @param orderRepository OrderRepository required to access the database.
     * @param shoppingCartItemRepository ShoppingCartItemRepository required to access the database.
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
    }

    /**
     * Generate predefined data.
     *
     * @author Cyrus Fung
     */
    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:VariableDeclarationUsageDistance"})
    public void generateData() {
        Customer customer0 = createCustomer("ada@language.com", "19801980", "0 Language Avenue", 0);
        Customer customer1 =
                createCustomer("basic@language.com", "19641964", "1 Language Avenue", 100);
        Customer customer2 = createCustomer("c@language.com", "19731973", "2 Language Avenue", 200);

        Employee employee0 =
                createEmployee("dart@language.com", "20112011", "3 Language Avenue", 300);
        Employee employee1 =
                createEmployee("erlang@language.com", "19861986", "4 Language Avenue", 400);
        Employee employee2 =
                createEmployee("fsharp@language.com", "20052005", "5 Language Avenue", 500);

        ClothingProduct clothingProduct0 = createClothingProduct("Go", 20.0f, "");
        ClothingProduct clothingProduct1 = createClothingProduct("Haskell", 40.0f, "");
        ClothingProduct clothingProduct2 = createClothingProduct("Jai", 60.0f, "");

        ClothingItem clothingItem0 = createClothingItem(Size.XS, Colour.BLACK, 0);
        ClothingItem clothingItem1 = createClothingItem(Size.S, Colour.BLUE, 10);
        ClothingItem clothingItem2 = createClothingItem(Size.M, Colour.BROWN, 20);
        ClothingItem clothingItem3 = createClothingItem(Size.M, Colour.GREEN, 30);
        ClothingItem clothingItem4 = createClothingItem(Size.L, Colour.PINK, 40);
        ClothingItem clothingItem5 = createClothingItem(Size.XL, Colour.PURPLE, 50);
        ClothingItem clothingItem6 = createClothingItem(Size.M, Colour.BLUE, 60);
        ClothingItem clothingItem7 = createClothingItem(Size.XS, Colour.GREEN, 70);

        Order order0 =
                createOrder(State.PURCHASED, 20f, ORDER_DATE, DELIVERY_DATE, "0 Language Avenue");
        Order order1 =
                createOrder(State.PURCHASED, 80f, ORDER_DATE, DELIVERY_DATE, "1 Language Avenue");
        Order order2 =
                createOrder(State.PURCHASED, 120f, ORDER_DATE, DELIVERY_DATE, "2 Language Avenue");
        Order order3 =
                createOrder(State.PURCHASED, 100f, ORDER_DATE, DELIVERY_DATE, "3 Language Avenue");
        Order order4 =
                createOrder(State.PURCHASED, 120f, ORDER_DATE, DELIVERY_DATE, "4 Language Avenue");

        OrderItem orderItem0 = createOrderItem(1, 20f); // order0
        OrderItem orderItem1 = createOrderItem(2, 20f); // order1
        OrderItem orderItem2 = createOrderItem(1, 40f); // order1
        OrderItem orderItem3 = createOrderItem(3, 40f); // order2
        OrderItem orderItem4 = createOrderItem(1, 40f); // order3
        OrderItem orderItem5 = createOrderItem(1, 60f); // order3
        OrderItem orderItem6 = createOrderItem(2, 60f); // order4

        clothingProduct0.addItem(clothingItem0);
        clothingProduct0.addItem(clothingItem1);
        clothingProduct0.addItem(clothingItem6);
        clothingProduct1.addItem(clothingItem2);
        clothingProduct1.addItem(clothingItem3);
        clothingProduct1.addItem(clothingItem7);
        clothingProduct2.addItem(clothingItem4);
        clothingProduct2.addItem(clothingItem5);

        orderItem0.setClothingItem(clothingItem0);
        orderItem1.setClothingItem(clothingItem1);
        orderItem2.setClothingItem(clothingItem2);
        orderItem3.setClothingItem(clothingItem3);
        orderItem4.setClothingItem(clothingItem3);
        orderItem5.setClothingItem(clothingItem4);
        orderItem6.setClothingItem(clothingItem5);

        order0.addItem(orderItem0);
        order1.addItem(orderItem1);
        order1.addItem(orderItem2);
        order2.addItem(orderItem3);
        order3.addItem(orderItem4);
        order3.addItem(orderItem5);
        order4.addItem(orderItem6);

        customer0.addPurchasedOrder(order0);
        customer1.addPurchasedOrder(order1);
        customer2.addPurchasedOrder(order2);
        employee0.addPurchasedOrder(order3);
        employee1.addPurchasedOrder(order4);

        ShoppingCartItem shoppingCartItem0 = createShoppingCartItem(2);
        shoppingCartItem0.setClothingItem(clothingItem0);
        shoppingCartItem0.setCustomer(customer0);

        ShoppingCartItem shoppingCartItem1 = createShoppingCartItem(4);
        shoppingCartItem1.setClothingItem(clothingItem1);
        shoppingCartItem1.setCustomer(customer1);

        ShoppingCartItem shoppingCartItem2 = createShoppingCartItem(6);
        shoppingCartItem2.setClothingItem(clothingItem2);
        shoppingCartItem2.setCustomer(customer1);

        ShoppingCartItem shoppingCartItem3 = createShoppingCartItem(2);
        shoppingCartItem3.setClothingItem(clothingItem2);
        shoppingCartItem3.setCustomer(customer2);

        ShoppingCartItem shoppingCartItem4 = createShoppingCartItem(4);
        shoppingCartItem4.setClothingItem(clothingItem3);
        shoppingCartItem4.setCustomer(employee0);

        ShoppingCartItem shoppingCartItem5 = createShoppingCartItem(6);
        shoppingCartItem5.setClothingItem(clothingItem4);
        shoppingCartItem5.setCustomer(employee2);

        customerRepository.save(customer0);
        customerRepository.save(customer1);
        customerRepository.save(customer2);
        employeeRepository.save(employee0);
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
        clothingProductRepository.save(clothingProduct0);
        clothingProductRepository.save(clothingProduct1);
        clothingProductRepository.save(clothingProduct2);
        clothingItemRepository.save(clothingItem0);
        clothingItemRepository.save(clothingItem1);
        clothingItemRepository.save(clothingItem2);
        clothingItemRepository.save(clothingItem3);
        clothingItemRepository.save(clothingItem4);
        clothingItemRepository.save(clothingItem5);
        orderRepository.save(order0);
        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);
        orderRepository.save(order4);
        orderItemRepository.save(orderItem0);
        orderItemRepository.save(orderItem1);
        orderItemRepository.save(orderItem2);
        orderItemRepository.save(orderItem3);
        orderItemRepository.save(orderItem4);
        orderItemRepository.save(orderItem5);
        orderItemRepository.save(orderItem6);
        shoppingCartItemRepository.save(shoppingCartItem0);
        shoppingCartItemRepository.save(shoppingCartItem1);
        shoppingCartItemRepository.save(shoppingCartItem2);
        shoppingCartItemRepository.save(shoppingCartItem3);
        shoppingCartItemRepository.save(shoppingCartItem4);
        shoppingCartItemRepository.save(shoppingCartItem5);
    }

    /**
     * Delete all data from database
     *
     * @author Cyrus Fung
     */
    public void deleteAllData() {
        shoppingCartItemRepository.deleteAll();
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        clothingItemRepository.deleteAll();
        clothingProductRepository.deleteAll();
        customerRepository.deleteAll();
        employeeRepository.deleteAll();
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

    private Customer createCustomer(
            String email, String password, String address, int numLoyaltyPoints) {
        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setPassword(password);
        customer.setAddress(address);
        customer.setNumLoyaltyPoints(numLoyaltyPoints);
        return customerRepository.save(customer);
    }

    private Employee createEmployee(
            String email, String password, String address, int numLoyaltyPoints) {
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

    private Order createOrder(
            State state, float price, Date orderDate, Date deliveryDate, String deliveryAddress) {
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
