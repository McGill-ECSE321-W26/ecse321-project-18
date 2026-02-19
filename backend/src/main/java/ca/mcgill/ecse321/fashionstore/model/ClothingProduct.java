/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8091.03bcab5b3 modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import java.util.*;

// line 44 "../../../../../../model.ump"
// line 92 "../../../../../../model.ump"
@Entity
public class ClothingProduct {

    // ------------------------
    // MEMBER VARIABLES
    // ------------------------

    // ClothingProduct Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
    private float price;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String image;

    // ClothingProduct Associations
    @OneToMany(mappedBy = "clothingProduct")
    private List<ClothingItem> items;

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public ClothingProduct(String aName, float aPrice) {
        id = 0;
        name = aName;
        price = aPrice;
        image = null;
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

    public boolean setName(String aName) {
        boolean wasSet = false;
        name = aName;
        wasSet = true;
        return wasSet;
    }

    public boolean setPrice(float aPrice) {
        boolean wasSet = false;
        price = aPrice;
        wasSet = true;
        return wasSet;
    }

    public boolean setImage(String aImage) {
        boolean wasSet = false;
        image = aImage;
        wasSet = true;
        return wasSet;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    /** image stored as String */
    public String getImage() {
        return image;
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

    /* Code from template association_MinimumNumberOfMethod */
    public static int minimumNumberOfItems() {
        return 0;
    }

    /* Code from template association_AddManyToOne */
    public ClothingItem addItem(
            ClothingItem.Size aSize, ClothingItem.Colour aColour, int aNumInStock) {
        return new ClothingItem(aSize, aColour, aNumInStock, this);
    }

    public boolean addItem(ClothingItem aItem) {
        boolean wasAdded = false;
        if (items.contains(aItem)) {
            return false;
        }
        ClothingProduct existingClothingProduct = aItem.getClothingProduct();
        boolean isNewClothingProduct =
                existingClothingProduct != null && !this.equals(existingClothingProduct);
        if (isNewClothingProduct) {
            aItem.setClothingProduct(this);
        } else {
            items.add(aItem);
        }
        wasAdded = true;
        return wasAdded;
    }

    public boolean removeItem(ClothingItem aItem) {
        boolean wasRemoved = false;
        // Unable to remove aItem, as it must always have a clothingProduct
        if (!this.equals(aItem.getClothingProduct())) {
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

    public void delete() {
        while (items.size() > 0) {
            ClothingItem aItem = items.get(items.size() - 1);
            aItem.delete();
            items.remove(aItem);
        }
    }

    public String toString() {
        return super.toString()
                + "["
                + "id"
                + ":"
                + getId()
                + ","
                + "name"
                + ":"
                + getName()
                + ","
                + "price"
                + ":"
                + getPrice()
                + ","
                + "image"
                + ":"
                + getImage()
                + "]";
    }
}
