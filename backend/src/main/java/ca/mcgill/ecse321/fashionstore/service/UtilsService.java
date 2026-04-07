package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem.Colour;
import ca.mcgill.ecse321.fashionstore.model.ClothingItem.Size;
import ca.mcgill.ecse321.fashionstore.model.ClothingProduct;
import ca.mcgill.ecse321.fashionstore.model.Customer;
import ca.mcgill.ecse321.fashionstore.model.Employee;
import ca.mcgill.ecse321.fashionstore.model.Order;
import ca.mcgill.ecse321.fashionstore.model.Order.State;
import ca.mcgill.ecse321.fashionstore.model.OrderItem;
import ca.mcgill.ecse321.fashionstore.model.Owner;
import ca.mcgill.ecse321.fashionstore.model.ShoppingCartItem;
import ca.mcgill.ecse321.fashionstore.repository.ClothingItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.ClothingProductRepository;
import ca.mcgill.ecse321.fashionstore.repository.CustomerRepository;
import ca.mcgill.ecse321.fashionstore.repository.EmployeeRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderItemRepository;
import ca.mcgill.ecse321.fashionstore.repository.OrderRepository;
import ca.mcgill.ecse321.fashionstore.repository.OwnerRepository;
import ca.mcgill.ecse321.fashionstore.repository.ShoppingCartItemRepository;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;
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
    private final OwnerRepository ownerRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private static final Date ORDER_DATE = Date.valueOf(LocalDate.now());
    private static final Date DELIVERY_DATE = Date.valueOf(LocalDate.now().plusDays(10));

    // Demo data
    private static Random random;
    private static final String IMAGE_DIR = "data";
    private static final int NUM_CUSTOMERS = 10;
    private static final int NUM_EMPLOYEES = 10;
    private static final int NUM_ORDERS = 3;
    private static final int NUM_ORDER_ITEMS = 5;
    private static final float MIN_PRICE = 50.0f;
    private static final float MAX_PRICE = 150.0f;
    private static final List<String> CUSTOMER_NAMES =
            List.of(
                    "James",
                    "Mary",
                    "Robert",
                    "Patricia",
                    "John",
                    "Jennifer",
                    "Michael",
                    "Linda",
                    "David",
                    "Elizabeth");
    private static final List<String> CUSTOMER_ADDRESSES =
            List.of(
                    "Smith",
                    "Johnson",
                    "Williams",
                    "Brown",
                    "Jones",
                    "Garcia",
                    "Miller",
                    "Davis",
                    "Rodriguez",
                    "Martinez");
    private static final List<String> EMPLOYEE_NAMES =
            List.of(
                    "William", "Barbara", "Richard", "Susan", "Joseph", "Jessica", "Thomas",
                    "Sarah", "Charles", "Karen");
    private static final List<String> EMPLOYEE_ADDRESSES =
            List.of(
                    "Hernandez",
                    "Lopez",
                    "Gonzalez",
                    "Wilson",
                    "Anderson",
                    "Thomas",
                    "Taylor",
                    "Moore",
                    "Jackson",
                    "Martin");
    private static final List<String> ADDRESS_SUFFIXES =
            List.of("Road", "Street", "Avenue", "Boulevard", "Drive", "Way");
    private static final String PASSWORD = "asdfasdf";

    /**
     * UtilsService constructor.
     *
     * @param clothingItemRepository ClothingItemRepository required to access the database.
     * @param clothingProductRepository ClothingProductRepository required to access the database.
     * @param customerRepository CustomerRepository required to access the database.
     * @param employeeRepository EmployeeRepository required to access the database.
     * @param orderItemRepository OrderItemRepository required to access the database.
     * @param orderRepository OrderRepository required to access the database.
     * @param ownerRepository OwnerRepository required to access the database.
     * @param shoppingCartItemRepository ShoppingCartItemRepository required to access the database.
     * @author Cyrus Fung
     */
    @Autowired
    public UtilsService(
            ClothingItemRepository clothingItemRepository,
            ClothingProductRepository clothingProductRepository,
            CustomerRepository customerRepository,
            EmployeeRepository employeeRepository,
            OrderItemRepository orderItemRepository,
            OrderRepository orderRepository,
            OwnerRepository ownerRepository,
            ShoppingCartItemRepository shoppingCartItemRepository) {
        this.clothingItemRepository = clothingItemRepository;
        this.clothingProductRepository = clothingProductRepository;
        this.customerRepository = customerRepository;
        this.employeeRepository = employeeRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.ownerRepository = ownerRepository;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        random = new Random();
    }

    /**
     * Generate predefined data.
     *
     * @author Cyrus Fung
     */
    public void generateData() {
        List<Owner> owners = ownerRepository.findAll();
        if (owners.isEmpty()) {
            Owner owner = new Owner();
            owner.setEmail("admin@fashionstore.com");
            owner.setPassword("security");
            ownerRepository.save(owner);
        }

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

        customerRepository.saveAll(List.of(customer0, customer1, customer2));
        employeeRepository.saveAll(List.of(employee0, employee1, employee2));
        clothingProductRepository.saveAll(
                List.of(clothingProduct0, clothingProduct1, clothingProduct2));
        clothingItemRepository.saveAll(
                List.of(
                        clothingItem0,
                        clothingItem1,
                        clothingItem2,
                        clothingItem3,
                        clothingItem4,
                        clothingItem5));
        orderRepository.saveAll(List.of(order0, order1, order2, order3, order4));
        orderItemRepository.saveAll(
                List.of(
                        orderItem0,
                        orderItem1,
                        orderItem2,
                        orderItem3,
                        orderItem4,
                        orderItem5,
                        orderItem6));
        shoppingCartItemRepository.saveAll(
                List.of(
                        shoppingCartItem0,
                        shoppingCartItem1,
                        shoppingCartItem2,
                        shoppingCartItem3,
                        shoppingCartItem4,
                        shoppingCartItem5));
    }

    /**
     * Generate demo data.
     *
     * @author Cyrus Fung
     */
    public void generateDemoData() {
        List<Owner> owners = ownerRepository.findAll();
        if (owners.isEmpty()) {
            Owner owner = new Owner();
            owner.setEmail("admin@stilton.com");
            owner.setPassword("security");
            ownerRepository.save(owner);
        }

        // generate clothing products and items
        List<ClothingProduct> clothingProducts = new ArrayList<>();
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] directory =
                    resolver.getResources(String.format("classpath:%s/*", IMAGE_DIR));
            for (Resource file : directory) {
                if (file.exists()) {
                    String name = getName(file.getFilename());
                    String b64 = imgToBase64(file);
                    ClothingProduct clothingProduct =
                            createClothingProduct(name, randFloat(MIN_PRICE, MAX_PRICE, 2), b64);
                    for (Colour colour : Colour.values()) {
                        for (Size size : Size.values()) {
                            ClothingItem clothingItem =
                                    createClothingItem(size, colour, random.nextInt(100));
                            clothingProduct.addItem(clothingItem);
                        }
                    }
                    clothingProducts.add(clothingProduct);
                }
            }
        } catch (IOException e) {
            throw new FashionStoreException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        // generate customers and employees
        List<Customer> customers = new ArrayList<>();
        List<Employee> employees = new ArrayList<>();
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < Integer.min(NUM_CUSTOMERS, CUSTOMER_NAMES.size()); i++) {
            // generate customer
            Customer customer =
                    createCustomer(
                            String.format("%s@email.com", CUSTOMER_NAMES.get(i).toLowerCase()),
                            PASSWORD,
                            String.format(
                                    "%d %s %s",
                                    random.nextInt(10, 1000),
                                    CUSTOMER_ADDRESSES.get(i),
                                    ADDRESS_SUFFIXES.get(random.nextInt(ADDRESS_SUFFIXES.size()))),
                            random.nextInt(2000));
            customers.add(customer);
            orders.addAll(generateOrders(clothingProducts, customer, NUM_ORDERS));
        }
        for (int i = 0; i < Integer.min(NUM_EMPLOYEES, EMPLOYEE_NAMES.size()); i++) {
            // generate employee
            Employee employee =
                    createEmployee(
                            String.format("%s@stilton.com", EMPLOYEE_NAMES.get(i).toLowerCase()),
                            PASSWORD,
                            String.format(
                                    "%d %s %s",
                                    random.nextInt(10, 1000),
                                    EMPLOYEE_ADDRESSES.get(i),
                                    ADDRESS_SUFFIXES.get(
                                            random.nextInt(0, ADDRESS_SUFFIXES.size()))),
                            random.nextInt(2000));
            employees.add(employee);
            orders.addAll(generateOrders(clothingProducts, employee, NUM_ORDERS));
        }

        // save
        customerRepository.saveAll(customers);
        employeeRepository.saveAll(employees);
        clothingProductRepository.saveAll(clothingProducts);
        for (ClothingProduct clothingProduct : clothingProducts) {
            clothingItemRepository.saveAll(clothingProduct.getItems());
        }
        orderRepository.saveAll(orders);
        for (Order order : orders) {
            orderItemRepository.saveAll(order.getItems());
        }
    }

    /**
     * Delete all data from database. Does not delete Owner.
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

    private List<Order> generateOrders(
            List<ClothingProduct> clothingProducts, Customer customer, int num) {
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            Order order =
                    createOrder(
                            State.PURCHASED,
                            0,
                            ORDER_DATE,
                            Date.valueOf(LocalDate.now().plusDays(random.nextInt(5, 20))),
                            customer.getAddress());
            float price = 0;
            for (int j = 0; j < NUM_ORDER_ITEMS; j++) {
                ClothingProduct clothingProduct =
                        clothingProducts.get(random.nextInt(clothingProducts.size()));
                ClothingItem clothingItem =
                        clothingProduct.getItem(random.nextInt(clothingProduct.getItems().size()));
                int quantity = random.nextInt(1, 11);
                OrderItem orderItem = createOrderItem(quantity, clothingProduct.getPrice());
                price += clothingProduct.getPrice() * quantity;
                orderItem.setClothingItem(clothingItem);
                order.addItem(orderItem);
            }
            order.setPrice(roundFloat(price, 2));
            customer.addPurchasedOrder(order);
        }
        return orders;
    }

    private ShoppingCartItem createShoppingCartItem(int quantity) {
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
        shoppingCartItem.setQuantity(quantity);
        return shoppingCartItemRepository.save(shoppingCartItem);
    }

    private String imgToBase64(Resource file) throws IOException {
        byte[] fileContents = file.getInputStream().readAllBytes();
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(fileContents);
    }

    private String getName(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder sb = new StringBuilder();
        boolean nextUpper = true;
        for (char c : input.toLowerCase().toCharArray()) {
            if (c == '_') {
                sb.append(' ');
                nextUpper = true;
            } else if (nextUpper) {
                sb.append(Character.toUpperCase(c));
                nextUpper = false;
            } else {
                sb.append(c);
            }
        }
        String name = sb.toString().trim();
        int extIndex = name.lastIndexOf('.');
        if (extIndex > 0) {
            name = name.substring(0, extIndex);
        }
        return name;
    }

    private static float randFloat(float min, float max, int round) {
        float num = random.nextFloat(min, max);
        return roundFloat(num, round);
    }

    private static float roundFloat(float num, int round) {
        BigDecimal bigDecimal = new BigDecimal(num);
        BigDecimal rounded = bigDecimal.setScale(round, RoundingMode.HALF_UP);
        return rounded.floatValue();
    }
}
