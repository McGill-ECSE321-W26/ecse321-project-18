/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.8043.819096d90 modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

import java.sql.Date;
import java.util.*;

// line 4 "../../../../../../model.ump"
// line 74 "../../../../../../model.ump"
public class FashionStore {

    // ------------------------
    // MEMBER VARIABLES
    // ------------------------

    // FashionStore Associations
    private Owner owner;
    private List<Customer> customers;
    private List<Order> orders;
    private List<ClothingProduct> products;

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public FashionStore() {
        customers = new ArrayList<Customer>();
        orders = new ArrayList<Order>();
        products = new ArrayList<ClothingProduct>();
    }

    // ------------------------
    // INTERFACE
    // ------------------------
    /* Code from template association_GetOne */
    public Owner getOwner() {
        return owner;
    }

    public boolean hasOwner() {
        boolean has = owner != null;
        return has;
    }

    /* Code from template association_GetMany */
    public Customer getCustomer(int index) {
        Customer aCustomer = customers.get(index);
        return aCustomer;
    }

    public List<Customer> getCustomers() {
        List<Customer> newCustomers = Collections.unmodifiableList(customers);
        return newCustomers;
    }

    public int numberOfCustomers() {
        int number = customers.size();
        return number;
    }

    public boolean hasCustomers() {
        boolean has = customers.size() > 0;
        return has;
    }

    public int indexOfCustomer(Customer aCustomer) {
        int index = customers.indexOf(aCustomer);
        return index;
    }

    /* Code from template association_GetMany */
    public Order getOrder(int index) {
        Order aOrder = orders.get(index);
        return aOrder;
    }

    public List<Order> getOrders() {
        List<Order> newOrders = Collections.unmodifiableList(orders);
        return newOrders;
    }

    public int numberOfOrders() {
        int number = orders.size();
        return number;
    }

    public boolean hasOrders() {
        boolean has = orders.size() > 0;
        return has;
    }

    public int indexOfOrder(Order aOrder) {
        int index = orders.indexOf(aOrder);
        return index;
    }

    /* Code from template association_GetMany */
    public ClothingProduct getProduct(int index) {
        ClothingProduct aProduct = products.get(index);
        return aProduct;
    }

    public List<ClothingProduct> getProducts() {
        List<ClothingProduct> newProducts = Collections.unmodifiableList(products);
        return newProducts;
    }

    public int numberOfProducts() {
        int number = products.size();
        return number;
    }

    public boolean hasProducts() {
        boolean has = products.size() > 0;
        return has;
    }

    public int indexOfProduct(ClothingProduct aProduct) {
        int index = products.indexOf(aProduct);
        return index;
    }

    /* Code from template association_SetOptionalOneToOne */
    public boolean setOwner(Owner aNewOwner) {
        boolean wasSet = false;
        if (owner != null && !owner.equals(aNewOwner) && equals(owner.getFashionStore())) {
            // Unable to setOwner, as existing owner would become an orphan
            return wasSet;
        }

        owner = aNewOwner;
        FashionStore anOldFashionStore = aNewOwner != null ? aNewOwner.getFashionStore() : null;

        if (!this.equals(anOldFashionStore)) {
            if (anOldFashionStore != null) {
                anOldFashionStore.owner = null;
            }
            if (owner != null) {
                owner.setFashionStore(this);
            }
        }
        wasSet = true;
        return wasSet;
    }

    /* Code from template association_MinimumNumberOfMethod */
    public static int minimumNumberOfCustomers() {
        return 0;
    }

    /* Code from template association_AddManyToOne */
    public Customer addCustomer(
            String aEmail, String aPassword, String aAddress, int aNumLoyaltyPoints) {
        return new Customer(aEmail, aPassword, aAddress, aNumLoyaltyPoints, this);
    }

    public boolean addCustomer(Customer aCustomer) {
        boolean wasAdded = false;
        if (customers.contains(aCustomer)) {
            return false;
        }
        FashionStore existingFashionStore = aCustomer.getFashionStore();
        boolean isNewFashionStore =
                existingFashionStore != null && !this.equals(existingFashionStore);
        if (isNewFashionStore) {
            aCustomer.setFashionStore(this);
        } else {
            customers.add(aCustomer);
        }
        wasAdded = true;
        return wasAdded;
    }

    public boolean removeCustomer(Customer aCustomer) {
        boolean wasRemoved = false;
        // Unable to remove aCustomer, as it must always have a fashionStore
        if (!this.equals(aCustomer.getFashionStore())) {
            customers.remove(aCustomer);
            wasRemoved = true;
        }
        return wasRemoved;
    }

    /* Code from template association_AddIndexControlFunctions */
    public boolean addCustomerAt(Customer aCustomer, int index) {
        boolean wasAdded = false;
        if (addCustomer(aCustomer)) {
            if (index < 0) {
                index = 0;
            }
            if (index > numberOfCustomers()) {
                index = numberOfCustomers() - 1;
            }
            customers.remove(aCustomer);
            customers.add(index, aCustomer);
            wasAdded = true;
        }
        return wasAdded;
    }

    public boolean addOrMoveCustomerAt(Customer aCustomer, int index) {
        boolean wasAdded = false;
        if (customers.contains(aCustomer)) {
            if (index < 0) {
                index = 0;
            }
            if (index > numberOfCustomers()) {
                index = numberOfCustomers() - 1;
            }
            customers.remove(aCustomer);
            customers.add(index, aCustomer);
            wasAdded = true;
        } else {
            wasAdded = addCustomerAt(aCustomer, index);
        }
        return wasAdded;
    }

    /* Code from template association_MinimumNumberOfMethod */
    public static int minimumNumberOfOrders() {
        return 0;
    }

    /* Code from template association_AddManyToOne */
    public Order addOrder(
            Order.State aState,
            Date aOrderDate,
            Date aDeliveryDate,
            String aDeliveryAddress,
            float aPrice) {
        return new Order(aState, aOrderDate, aDeliveryDate, aDeliveryAddress, aPrice, this);
    }

    public boolean addOrder(Order aOrder) {
        boolean wasAdded = false;
        if (orders.contains(aOrder)) {
            return false;
        }
        FashionStore existingFashionStore = aOrder.getFashionStore();
        boolean isNewFashionStore =
                existingFashionStore != null && !this.equals(existingFashionStore);
        if (isNewFashionStore) {
            aOrder.setFashionStore(this);
        } else {
            orders.add(aOrder);
        }
        wasAdded = true;
        return wasAdded;
    }

    public boolean removeOrder(Order aOrder) {
        boolean wasRemoved = false;
        // Unable to remove aOrder, as it must always have a fashionStore
        if (!this.equals(aOrder.getFashionStore())) {
            orders.remove(aOrder);
            wasRemoved = true;
        }
        return wasRemoved;
    }

    /* Code from template association_AddIndexControlFunctions */
    public boolean addOrderAt(Order aOrder, int index) {
        boolean wasAdded = false;
        if (addOrder(aOrder)) {
            if (index < 0) {
                index = 0;
            }
            if (index > numberOfOrders()) {
                index = numberOfOrders() - 1;
            }
            orders.remove(aOrder);
            orders.add(index, aOrder);
            wasAdded = true;
        }
        return wasAdded;
    }

    public boolean addOrMoveOrderAt(Order aOrder, int index) {
        boolean wasAdded = false;
        if (orders.contains(aOrder)) {
            if (index < 0) {
                index = 0;
            }
            if (index > numberOfOrders()) {
                index = numberOfOrders() - 1;
            }
            orders.remove(aOrder);
            orders.add(index, aOrder);
            wasAdded = true;
        } else {
            wasAdded = addOrderAt(aOrder, index);
        }
        return wasAdded;
    }

    /* Code from template association_MinimumNumberOfMethod */
    public static int minimumNumberOfProducts() {
        return 0;
    }

    /* Code from template association_AddManyToOne */
    public ClothingProduct addProduct(String aName, float aPrice) {
        return new ClothingProduct(aName, aPrice, this);
    }

    public boolean addProduct(ClothingProduct aProduct) {
        boolean wasAdded = false;
        if (products.contains(aProduct)) {
            return false;
        }
        FashionStore existingFashionStore = aProduct.getFashionStore();
        boolean isNewFashionStore =
                existingFashionStore != null && !this.equals(existingFashionStore);
        if (isNewFashionStore) {
            aProduct.setFashionStore(this);
        } else {
            products.add(aProduct);
        }
        wasAdded = true;
        return wasAdded;
    }

    public boolean removeProduct(ClothingProduct aProduct) {
        boolean wasRemoved = false;
        // Unable to remove aProduct, as it must always have a fashionStore
        if (!this.equals(aProduct.getFashionStore())) {
            products.remove(aProduct);
            wasRemoved = true;
        }
        return wasRemoved;
    }

    /* Code from template association_AddIndexControlFunctions */
    public boolean addProductAt(ClothingProduct aProduct, int index) {
        boolean wasAdded = false;
        if (addProduct(aProduct)) {
            if (index < 0) {
                index = 0;
            }
            if (index > numberOfProducts()) {
                index = numberOfProducts() - 1;
            }
            products.remove(aProduct);
            products.add(index, aProduct);
            wasAdded = true;
        }
        return wasAdded;
    }

    public boolean addOrMoveProductAt(ClothingProduct aProduct, int index) {
        boolean wasAdded = false;
        if (products.contains(aProduct)) {
            if (index < 0) {
                index = 0;
            }
            if (index > numberOfProducts()) {
                index = numberOfProducts() - 1;
            }
            products.remove(aProduct);
            products.add(index, aProduct);
            wasAdded = true;
        } else {
            wasAdded = addProductAt(aProduct, index);
        }
        return wasAdded;
    }

    public void delete() {
        Owner existingOwner = owner;
        owner = null;
        if (existingOwner != null) {
            existingOwner.delete();
            existingOwner.setFashionStore(null);
        }
        while (customers.size() > 0) {
            Customer aCustomer = customers.get(customers.size() - 1);
            aCustomer.delete();
            customers.remove(aCustomer);
        }

        while (orders.size() > 0) {
            Order aOrder = orders.get(orders.size() - 1);
            aOrder.delete();
            orders.remove(aOrder);
        }

        while (products.size() > 0) {
            ClothingProduct aProduct = products.get(products.size() - 1);
            aProduct.delete();
            products.remove(aProduct);
        }
    }
}
