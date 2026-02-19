/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8108.3ce48223a modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import java.util.*;

// line 15 "../../../../../../model.ump"
// line 77 "../../../../../../model.ump"
@Entity
public class Customer extends Account {

    // ------------------------
    // MEMBER VARIABLES
    // ------------------------

    // Customer Attributes
    private String address;
    private int numLoyaltyPoints;

    // Customer Associations
    @ManyToMany private List<ClothingItem> shoppingCart;

    @OneToMany(mappedBy = "customer")
    private List<Order> purchasedOrders;

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public Customer() {
        super();
        address = null;
        numLoyaltyPoints = 0;
        shoppingCart = new ArrayList<ClothingItem>();
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
    public ClothingItem getShoppingCart(int index) {
        ClothingItem aShoppingCart = shoppingCart.get(index);
        return aShoppingCart;
    }

    public List<ClothingItem> getShoppingCart() {
        List<ClothingItem> newShoppingCart = Collections.unmodifiableList(shoppingCart);
        return newShoppingCart;
    }

    public int numberOfShoppingCart() {
        int number = shoppingCart.size();
        return number;
    }

    public boolean hasShoppingCart() {
        boolean has = shoppingCart.size() > 0;
        return has;
    }

    public int indexOfShoppingCart(ClothingItem aShoppingCart) {
        int index = shoppingCart.indexOf(aShoppingCart);
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
    public static int minimumNumberOfShoppingCart() {
        return 0;
    }

    /* Code from template association_AddUnidirectionalMany */
    public boolean addShoppingCart(ClothingItem aShoppingCart) {
        boolean wasAdded = false;
        if (shoppingCart.contains(aShoppingCart)) {
            return false;
        }
        shoppingCart.add(aShoppingCart);
        wasAdded = true;
        return wasAdded;
    }

    public boolean removeShoppingCart(ClothingItem aShoppingCart) {
        boolean wasRemoved = false;
        if (shoppingCart.contains(aShoppingCart)) {
            shoppingCart.remove(aShoppingCart);
            wasRemoved = true;
        }
        return wasRemoved;
    }

    /* Code from template association_AddIndexControlFunctions */
    public boolean addShoppingCartAt(ClothingItem aShoppingCart, int index) {
        boolean wasAdded = false;
        if (addShoppingCart(aShoppingCart)) {
            if (index < 0) {
                index = 0;
            }
            if (index > numberOfShoppingCart()) {
                index = numberOfShoppingCart() - 1;
            }
            shoppingCart.remove(aShoppingCart);
            shoppingCart.add(index, aShoppingCart);
            wasAdded = true;
        }
        return wasAdded;
    }

    public boolean addOrMoveShoppingCartAt(ClothingItem aShoppingCart, int index) {
        boolean wasAdded = false;
        if (shoppingCart.contains(aShoppingCart)) {
            if (index < 0) {
                index = 0;
            }
            if (index > numberOfShoppingCart()) {
                index = numberOfShoppingCart() - 1;
            }
            shoppingCart.remove(aShoppingCart);
            shoppingCart.add(index, aShoppingCart);
            wasAdded = true;
        } else {
            wasAdded = addShoppingCartAt(aShoppingCart, index);
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
        shoppingCart.clear();
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
