/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8183.32a6408a9 modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

// line 53 "../../../../../../model.ump"
// line 113 "../../../../../../model.ump"
@Entity
@Table(
        name = "shopping_cart_item",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "cartitem_customerid_clothingid",
                    columnNames = {"customer_id", "clothing_item_id"})
        })
public class ShoppingCartItem {

    // ------------------------
    // MEMBER VARIABLES
    // ------------------------

    // ShoppingCartItem Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int quantity;

    // ShoppingCartItem Associations
    @ManyToOne private ClothingItem clothingItem;
    @ManyToOne private Customer customer;

    // Helper Variables
    @Transient private boolean canSetId;

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public ShoppingCartItem() {
        canSetId = true;
        quantity = 0;
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

    public int getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
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
    public Customer getCustomer() {
        return customer;
    }

    public boolean hasCustomer() {
        boolean has = customer != null;
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
    public boolean setCustomer(Customer aCustomer) {
        boolean wasSet = false;
        Customer existingCustomer = customer;
        customer = aCustomer;
        if (existingCustomer != null && !existingCustomer.equals(aCustomer)) {
            existingCustomer.removeShoppingCartItem(this);
        }
        if (aCustomer != null) {
            aCustomer.addShoppingCartItem(this);
        }
        wasSet = true;
        return wasSet;
    }

    public void delete() {
        clothingItem = null;
        if (customer != null) {
            Customer placeholderCustomer = customer;
            this.customer = null;
            placeholderCustomer.removeShoppingCartItem(this);
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
                + "]"
                + System.getProperties().getProperty("line.separator")
                + "  "
                + "clothingItem = "
                + (getClothingItem() != null
                        ? Integer.toHexString(System.identityHashCode(getClothingItem()))
                        : "null")
                + System.getProperties().getProperty("line.separator")
                + "  "
                + "customer = "
                + (getCustomer() != null
                        ? Integer.toHexString(System.identityHashCode(getCustomer()))
                        : "null");
    }
}
