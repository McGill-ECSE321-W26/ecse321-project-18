/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8183.32a6408a9 modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.*;

// line 16 "../../../../../../model.ump"
// line 93 "../../../../../../model.ump"
@Entity
public class Customer extends Account {

    // ------------------------
    // MEMBER VARIABLES
    // ------------------------

    // Customer Attributes
    private String address;
    private int numLoyaltyPoints;

    // Customer Associations
    @ManyToMany(cascade = CascadeType.ALL)
    private List<ShoppingCartItem> shoppingCartItems;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> purchasedOrders;

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public Customer() {
        super();
        address = null;
        numLoyaltyPoints = 0;
        shoppingCartItems = new ArrayList<ShoppingCartItem>();
        purchasedOrders = new ArrayList<Order>();
    }

    // ------------------------
    // INTERFACE
    // ------------------------

    public boolean setAddress(String aAddress) {
        boolean wasSet = false;
        address = aAddress;
        wasSet = true;
        return wasSet;
    }

    public boolean setNumLoyaltyPoints(int aNumLoyaltyPoints) {
        boolean wasSet = false;
        numLoyaltyPoints = aNumLoyaltyPoints;
        wasSet = true;
        return wasSet;
    }

    public String getAddress() {
        return address;
    }

    public int getNumLoyaltyPoints() {
        return numLoyaltyPoints;
    }

    /* Code from template association_GetMany */
    public ShoppingCartItem getShoppingCartItem(int index) {
        ShoppingCartItem aShoppingCartItem = shoppingCartItems.get(index);
        return aShoppingCartItem;
    }

    public List<ShoppingCartItem> getShoppingCartItems() {
        List<ShoppingCartItem> newShoppingCartItems =
                Collections.unmodifiableList(shoppingCartItems);
        return newShoppingCartItems;
    }

    public int numberOfShoppingCartItems() {
        int number = shoppingCartItems.size();
        return number;
    }

    public boolean hasShoppingCartItems() {
        boolean has = shoppingCartItems.size() > 0;
        return has;
    }

    public int indexOfShoppingCartItem(ShoppingCartItem aShoppingCartItem) {
        int index = shoppingCartItems.indexOf(aShoppingCartItem);
        return index;
    }

    /* Code from template association_GetMany */
    public Order getPurchasedOrder(int index) {
        Order aPurchasedOrder = purchasedOrders.get(index);
        return aPurchasedOrder;
    }

    /** an Order can have no Customer if the Customer's account was deleted */
    public List<Order> getPurchasedOrders() {
        List<Order> newPurchasedOrders = Collections.unmodifiableList(purchasedOrders);
        return newPurchasedOrders;
    }

    public int numberOfPurchasedOrders() {
        int number = purchasedOrders.size();
        return number;
    }

    public boolean hasPurchasedOrders() {
        boolean has = purchasedOrders.size() > 0;
        return has;
    }

    public int indexOfPurchasedOrder(Order aPurchasedOrder) {
        int index = purchasedOrders.indexOf(aPurchasedOrder);
        return index;
    }

    /* Code from template association_MinimumNumberOfMethod */
    public static int minimumNumberOfShoppingCartItems() {
        return 0;
    }

    /* Code from template association_AddManyToOptionalOne */
    public boolean addShoppingCartItem(ShoppingCartItem aShoppingCartItem) {
        boolean wasAdded = false;
        if (shoppingCartItems.contains(aShoppingCartItem)) {
            return false;
        }
        Customer existingCustomer = aShoppingCartItem.getCustomer();
        if (existingCustomer == null) {
            aShoppingCartItem.setCustomer(this);
        } else if (!this.equals(existingCustomer)) {
            existingCustomer.removeShoppingCartItem(aShoppingCartItem);
            addShoppingCartItem(aShoppingCartItem);
        } else {
            shoppingCartItems.add(aShoppingCartItem);
        }
        wasAdded = true;
        return wasAdded;
    }

    public boolean removeShoppingCartItem(ShoppingCartItem aShoppingCartItem) {
        boolean wasRemoved = false;
        if (shoppingCartItems.contains(aShoppingCartItem)) {
            shoppingCartItems.remove(aShoppingCartItem);
            aShoppingCartItem.setCustomer(null);
            wasRemoved = true;
        }
        return wasRemoved;
    }

    /* Code from template association_AddIndexControlFunctions */
    public boolean addShoppingCartItemAt(ShoppingCartItem aShoppingCartItem, int index) {
        boolean wasAdded = false;
        if (addShoppingCartItem(aShoppingCartItem)) {
            if (index < 0) {
                index = 0;
            }
            if (index > numberOfShoppingCartItems()) {
                index = numberOfShoppingCartItems() - 1;
            }
            shoppingCartItems.remove(aShoppingCartItem);
            shoppingCartItems.add(index, aShoppingCartItem);
            wasAdded = true;
        }
        return wasAdded;
    }

    public boolean addOrMoveShoppingCartItemAt(ShoppingCartItem aShoppingCartItem, int index) {
        boolean wasAdded = false;
        if (shoppingCartItems.contains(aShoppingCartItem)) {
            if (index < 0) {
                index = 0;
            }
            if (index > numberOfShoppingCartItems()) {
                index = numberOfShoppingCartItems() - 1;
            }
            shoppingCartItems.remove(aShoppingCartItem);
            shoppingCartItems.add(index, aShoppingCartItem);
            wasAdded = true;
        } else {
            wasAdded = addShoppingCartItemAt(aShoppingCartItem, index);
        }
        return wasAdded;
    }

    /* Code from template association_MinimumNumberOfMethod */
    public static int minimumNumberOfPurchasedOrders() {
        return 0;
    }

    /* Code from template association_AddManyToOptionalOne */
    public boolean addPurchasedOrder(Order aPurchasedOrder) {
        boolean wasAdded = false;
        if (purchasedOrders.contains(aPurchasedOrder)) {
            return false;
        }
        Customer existingCustomer = aPurchasedOrder.getCustomer();
        if (existingCustomer == null) {
            aPurchasedOrder.setCustomer(this);
        } else if (!this.equals(existingCustomer)) {
            existingCustomer.removePurchasedOrder(aPurchasedOrder);
            addPurchasedOrder(aPurchasedOrder);
        } else {
            purchasedOrders.add(aPurchasedOrder);
        }
        wasAdded = true;
        return wasAdded;
    }

    public boolean removePurchasedOrder(Order aPurchasedOrder) {
        boolean wasRemoved = false;
        if (purchasedOrders.contains(aPurchasedOrder)) {
            purchasedOrders.remove(aPurchasedOrder);
            aPurchasedOrder.setCustomer(null);
            wasRemoved = true;
        }
        return wasRemoved;
    }

    /* Code from template association_AddIndexControlFunctions */
    public boolean addPurchasedOrderAt(Order aPurchasedOrder, int index) {
        boolean wasAdded = false;
        if (addPurchasedOrder(aPurchasedOrder)) {
            if (index < 0) {
                index = 0;
            }
            if (index > numberOfPurchasedOrders()) {
                index = numberOfPurchasedOrders() - 1;
            }
            purchasedOrders.remove(aPurchasedOrder);
            purchasedOrders.add(index, aPurchasedOrder);
            wasAdded = true;
        }
        return wasAdded;
    }

    public boolean addOrMovePurchasedOrderAt(Order aPurchasedOrder, int index) {
        boolean wasAdded = false;
        if (purchasedOrders.contains(aPurchasedOrder)) {
            if (index < 0) {
                index = 0;
            }
            if (index > numberOfPurchasedOrders()) {
                index = numberOfPurchasedOrders() - 1;
            }
            purchasedOrders.remove(aPurchasedOrder);
            purchasedOrders.add(index, aPurchasedOrder);
            wasAdded = true;
        } else {
            wasAdded = addPurchasedOrderAt(aPurchasedOrder, index);
        }
        return wasAdded;
    }

    public void delete() {
        while (shoppingCartItems.size() > 0) {
            ShoppingCartItem aShoppingCartItem =
                    shoppingCartItems.get(shoppingCartItems.size() - 1);
            aShoppingCartItem.delete();
            shoppingCartItems.remove(aShoppingCartItem);
        }

        while (!purchasedOrders.isEmpty()) {
            purchasedOrders.get(0).setCustomer(null);
        }
        super.delete();
    }

    public String toString() {
        return super.toString()
                + "["
                + "address"
                + ":"
                + getAddress()
                + ","
                + "numLoyaltyPoints"
                + ":"
                + getNumLoyaltyPoints()
                + "]";
    }
}
