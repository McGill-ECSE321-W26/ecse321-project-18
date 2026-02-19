/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8108.3ce48223a modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Date;
import java.util.*;

// line 31 "../../../../../../model.ump"
// line 87 "../../../../../../model.ump"
@Entity
@Table(name = "store_order") // ORDER is a reserved word in PostgreSQL
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
    // MEMBER VARIABLES
    // ------------------------

    // Order Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Enumerated(EnumType.STRING)
    private State state;

    private Date orderDate;
    private Date deliveryDate;
    private String deliveryAddress;
    private float price;

    // Order Associations
    @ManyToMany private List<ClothingItem> items;
    @ManyToOne private Customer customer;
    @ManyToOne private Employee employee;

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public Order() {
        id = 0;
        orderDate = null;
        deliveryDate = null;
        deliveryAddress = null;
        price = 0.0f;
        items = new ArrayList<ClothingItem>();
    }

    // ------------------------
    // INTERFACE
    // ------------------------

    public boolean setId(int aId) {
        boolean wasSet = false;
        id = aId;
        wasSet = true;
        return wasSet;
    }

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

    public int getId() {
        return id;
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
