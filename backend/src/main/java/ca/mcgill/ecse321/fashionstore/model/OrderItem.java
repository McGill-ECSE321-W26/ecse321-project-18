/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8183.32a6408a9 modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

// line 45 "../../../../../../model.ump"
// line 108 "../../../../../../model.ump"
@Entity
public class OrderItem {

    // ------------------------
    // MEMBER VARIABLES
    // ------------------------

    // OrderItem Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int quantity;
    private float purchasePrice;

    // OrderItem Associations
    @ManyToOne private ClothingItem clothingItem;
    @ManyToOne private Order order;

    // Helper Variables
    @Transient private boolean canSetId;

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public OrderItem() {
        canSetId = true;
        quantity = 0;
        purchasePrice = 0.0f;
    }

    // ------------------------
    // INTERFACE
    // ------------------------
    /* Code from template attribute_SetImmutable */
    public boolean setId(int aId) {
        boolean wasSet = false;
        if (!canSetId) {
            return false;
        }
        canSetId = false;
        id = aId;
        wasSet = true;
        return wasSet;
    }

    public boolean setQuantity(int aQuantity) {
        boolean wasSet = false;
        quantity = aQuantity;
        wasSet = true;
        return wasSet;
    }

    public boolean setPurchasePrice(float aPurchasePrice) {
        boolean wasSet = false;
        purchasePrice = aPurchasePrice;
        wasSet = true;
        return wasSet;
    }

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    /** price of a single item at purchase */
    public float getPurchasePrice() {
        return purchasePrice;
    }

    /* Code from template association_GetOne */
    public ClothingItem getClothingItem() {
        return clothingItem;
    }

    public boolean hasClothingItem() {
        boolean has = clothingItem != null;
        return has;
    }

    /* Code from template association_GetOne */
    public Order getOrder() {
        return order;
    }

    public boolean hasOrder() {
        boolean has = order != null;
        return has;
    }

    /* Code from template association_SetUnidirectionalOptionalOne */
    public boolean setClothingItem(ClothingItem aNewClothingItem) {
        boolean wasSet = false;
        clothingItem = aNewClothingItem;
        wasSet = true;
        return wasSet;
    }

    /* Code from template association_SetOptionalOneToMany */
    public boolean setOrder(Order aOrder) {
        boolean wasSet = false;
        Order existingOrder = order;
        order = aOrder;
        if (existingOrder != null && !existingOrder.equals(aOrder)) {
            existingOrder.removeItem(this);
        }
        if (aOrder != null) {
            aOrder.addItem(this);
        }
        wasSet = true;
        return wasSet;
    }

    public void delete() {
        clothingItem = null;
        if (order != null) {
            Order placeholderOrder = order;
            this.order = null;
            placeholderOrder.removeItem(this);
        }
    }

    public String toString() {
        return super.toString()
                + "["
                + "id"
                + ":"
                + getId()
                + ","
                + "quantity"
                + ":"
                + getQuantity()
                + ","
                + "purchasePrice"
                + ":"
                + getPurchasePrice()
                + "]"
                + System.getProperties().getProperty("line.separator")
                + "  "
                + "clothingItem = "
                + (getClothingItem() != null
                        ? Integer.toHexString(System.identityHashCode(getClothingItem()))
                        : "null")
                + System.getProperties().getProperty("line.separator")
                + "  "
                + "order = "
                + (getOrder() != null
                        ? Integer.toHexString(System.identityHashCode(getOrder()))
                        : "null");
    }
}
