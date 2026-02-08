/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.8043.819096d90 modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

import java.sql.Date;
import java.util.*;

// line 38 "../../../../../../model.ump"
// line 99 "../../../../../../model.ump"
public class Order {

    // ------------------------
    // ENUMERATIONS
    // ------------------------

    public enum State {
        PURCHASED,
        ASSIGNED,
        PREPARED,
        DELIVERED,
        CANCELLED
    }

    // ------------------------
    // STATIC VARIABLES
    // ------------------------

    private static int nextId = 1;

    // ------------------------
    // MEMBER VARIABLES
    // ------------------------

    // Order Attributes
    private State state;
    private Date orderDate;
    private Date deliveryDate;
    private String deliveryAddress;
    private float price;

    // Autounique Attributes
    private int id;

    // Order Associations
    private List<ClothingItem> items;
    private FashionStore fashionStore;
    private Customer customer;
    private Employee employee;

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public Order(
            State aState,
            Date aOrderDate,
            Date aDeliveryDate,
            String aDeliveryAddress,
            float aPrice,
            FashionStore aFashionStore) {
        state = aState;
        orderDate = aOrderDate;
        deliveryDate = aDeliveryDate;
        deliveryAddress = aDeliveryAddress;
        price = aPrice;
        id = nextId++;
        items = new ArrayList<ClothingItem>();
        boolean didAddFashionStore = setFashionStore(aFashionStore);
        if (!didAddFashionStore) {
            throw new RuntimeException(
                    "Unable to create order due to fashionStore. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
        }
    }

    // ------------------------
    // INTERFACE
    // ------------------------

    public boolean setState(State aState) {
        boolean wasSet = false;
        state = aState;
        wasSet = true;
        return wasSet;
    }

    public boolean setOrderDate(Date aOrderDate) {
        boolean wasSet = false;
        orderDate = aOrderDate;
        wasSet = true;
        return wasSet;
    }

    public boolean setDeliveryDate(Date aDeliveryDate) {
        boolean wasSet = false;
        deliveryDate = aDeliveryDate;
        wasSet = true;
        return wasSet;
    }

    public boolean setDeliveryAddress(String aDeliveryAddress) {
        boolean wasSet = false;
        deliveryAddress = aDeliveryAddress;
        wasSet = true;
        return wasSet;
    }

    public boolean setPrice(float aPrice) {
        boolean wasSet = false;
        price = aPrice;
        wasSet = true;
        return wasSet;
    }

    public State getState() {
        return state;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public float getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }

    /* Code from template association_GetMany */
    public ClothingItem getItem(int index) {
        ClothingItem aItem = items.get(index);
        return aItem;
    }

    public List<ClothingItem> getItems() {
        List<ClothingItem> newItems = Collections.unmodifiableList(items);
        return newItems;
    }

    public int numberOfItems() {
        int number = items.size();
        return number;
    }

    public boolean hasItems() {
        boolean has = items.size() > 0;
        return has;
    }

    public int indexOfItem(ClothingItem aItem) {
        int index = items.indexOf(aItem);
        return index;
    }

    /* Code from template association_GetOne */
    public FashionStore getFashionStore() {
        return fashionStore;
    }

    /* Code from template association_GetOne */
    public Customer getCustomer() {
        return customer;
    }

    public boolean hasCustomer() {
        boolean has = customer != null;
        return has;
    }

    /* Code from template association_GetOne */
    public Employee getEmployee() {
        return employee;
    }

    public boolean hasEmployee() {
        boolean has = employee != null;
        return has;
    }

    /* Code from template association_MinimumNumberOfMethod */
    public static int minimumNumberOfItems() {
        return 0;
    }

    /* Code from template association_AddUnidirectionalMany */
    public boolean addItem(ClothingItem aItem) {
        boolean wasAdded = false;
        if (items.contains(aItem)) {
            return false;
        }
        items.add(aItem);
        wasAdded = true;
        return wasAdded;
    }

    public boolean removeItem(ClothingItem aItem) {
        boolean wasRemoved = false;
        if (items.contains(aItem)) {
            items.remove(aItem);
            wasRemoved = true;
        }
        return wasRemoved;
    }

    /* Code from template association_AddIndexControlFunctions */
    public boolean addItemAt(ClothingItem aItem, int index) {
        boolean wasAdded = false;
        if (addItem(aItem)) {
            if (index < 0) {
                index = 0;
            }
            if (index > numberOfItems()) {
                index = numberOfItems() - 1;
            }
            items.remove(aItem);
            items.add(index, aItem);
            wasAdded = true;
        }
        return wasAdded;
    }

    public boolean addOrMoveItemAt(ClothingItem aItem, int index) {
        boolean wasAdded = false;
        if (items.contains(aItem)) {
            if (index < 0) {
                index = 0;
            }
            if (index > numberOfItems()) {
                index = numberOfItems() - 1;
            }
            items.remove(aItem);
            items.add(index, aItem);
            wasAdded = true;
        } else {
            wasAdded = addItemAt(aItem, index);
        }
        return wasAdded;
    }

    /* Code from template association_SetOneToMany */
    public boolean setFashionStore(FashionStore aFashionStore) {
        boolean wasSet = false;
        if (aFashionStore == null) {
            return wasSet;
        }

        FashionStore existingFashionStore = fashionStore;
        fashionStore = aFashionStore;
        if (existingFashionStore != null && !existingFashionStore.equals(aFashionStore)) {
            existingFashionStore.removeOrder(this);
        }
        fashionStore.addOrder(this);
        wasSet = true;
        return wasSet;
    }

    /* Code from template association_SetOptionalOneToMany */
    public boolean setCustomer(Customer aCustomer) {
        boolean wasSet = false;
        Customer existingCustomer = customer;
        customer = aCustomer;
        if (existingCustomer != null && !existingCustomer.equals(aCustomer)) {
            existingCustomer.removePurchasedOrder(this);
        }
        if (aCustomer != null) {
            aCustomer.addPurchasedOrder(this);
        }
        wasSet = true;
        return wasSet;
    }

    /* Code from template association_SetOptionalOneToMany */
    public boolean setEmployee(Employee aEmployee) {
        boolean wasSet = false;
        Employee existingEmployee = employee;
        employee = aEmployee;
        if (existingEmployee != null && !existingEmployee.equals(aEmployee)) {
            existingEmployee.removeAssignedOrder(this);
        }
        if (aEmployee != null) {
            aEmployee.addAssignedOrder(this);
        }
        wasSet = true;
        return wasSet;
    }

    public void delete() {
        items.clear();
        FashionStore placeholderFashionStore = fashionStore;
        this.fashionStore = null;
        if (placeholderFashionStore != null) {
            placeholderFashionStore.removeOrder(this);
        }
        if (customer != null) {
            Customer placeholderCustomer = customer;
            this.customer = null;
            placeholderCustomer.removePurchasedOrder(this);
        }
        if (employee != null) {
            Employee placeholderEmployee = employee;
            this.employee = null;
            placeholderEmployee.removeAssignedOrder(this);
        }
    }

    public String toString() {
        return super.toString()
                + "["
                + "id"
                + ":"
                + getId()
                + ","
                + "deliveryAddress"
                + ":"
                + getDeliveryAddress()
                + ","
                + "price"
                + ":"
                + getPrice()
                + "]"
                + System.getProperties().getProperty("line.separator")
                + "  "
                + "state"
                + "="
                + (getState() != null
                        ? !getState().equals(this)
                                ? getState().toString().replaceAll("  ", "    ")
                                : "this"
                        : "null")
                + System.getProperties().getProperty("line.separator")
                + "  "
                + "orderDate"
                + "="
                + (getOrderDate() != null
                        ? !getOrderDate().equals(this)
                                ? getOrderDate().toString().replaceAll("  ", "    ")
                                : "this"
                        : "null")
                + System.getProperties().getProperty("line.separator")
                + "  "
                + "deliveryDate"
                + "="
                + (getDeliveryDate() != null
                        ? !getDeliveryDate().equals(this)
                                ? getDeliveryDate().toString().replaceAll("  ", "    ")
                                : "this"
                        : "null")
                + System.getProperties().getProperty("line.separator")
                + "  "
                + "fashionStore = "
                + (getFashionStore() != null
                        ? Integer.toHexString(System.identityHashCode(getFashionStore()))
                        : "null")
                + System.getProperties().getProperty("line.separator")
                + "  "
                + "customer = "
                + (getCustomer() != null
                        ? Integer.toHexString(System.identityHashCode(getCustomer()))
                        : "null")
                + System.getProperties().getProperty("line.separator")
                + "  "
                + "employee = "
                + (getEmployee() != null
                        ? Integer.toHexString(System.identityHashCode(getEmployee()))
                        : "null");
    }
}
