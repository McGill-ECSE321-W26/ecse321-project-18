/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.8043.819096d90 modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

import java.util.*;

// line 51 "../../../../../../model.ump"
// line 104 "../../../../../../model.ump"
public class ClothingProduct {

    // ------------------------
    // STATIC VARIABLES
    // ------------------------

    private static int nextId = 1;

    // ------------------------
    // MEMBER VARIABLES
    // ------------------------

    // ClothingProduct Attributes
    private String name;
    private float price;
    private String image;

    // Autounique Attributes
    private int id;

    // ClothingProduct Associations
    private List<ClothingItem> items;
    private FashionStore fashionStore;

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public ClothingProduct(String aName, float aPrice, FashionStore aFashionStore) {
        name = aName;
        price = aPrice;
        image = null;
        id = nextId++;
        items = new ArrayList<ClothingItem>();
        boolean didAddFashionStore = setFashionStore(aFashionStore);
        if (!didAddFashionStore) {
            throw new RuntimeException(
                    "Unable to create product due to fashionStore. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
        }
    }

    // ------------------------
    // INTERFACE
    // ------------------------

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

    /* Code from template association_SetOneToMany */
    public boolean setFashionStore(FashionStore aFashionStore) {
        boolean wasSet = false;
        if (aFashionStore == null) {
            return wasSet;
        }

        FashionStore existingFashionStore = fashionStore;
        fashionStore = aFashionStore;
        if (existingFashionStore != null && !existingFashionStore.equals(aFashionStore)) {
            existingFashionStore.removeProduct(this);
        }
        fashionStore.addProduct(this);
        wasSet = true;
        return wasSet;
    }

    public void delete() {
        while (items.size() > 0) {
            ClothingItem aItem = items.get(items.size() - 1);
            aItem.delete();
            items.remove(aItem);
        }

        FashionStore placeholderFashionStore = fashionStore;
        this.fashionStore = null;
        if (placeholderFashionStore != null) {
            placeholderFashionStore.removeProduct(this);
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
                + "]"
                + System.getProperties().getProperty("line.separator")
                + "  "
                + "fashionStore = "
                + (getFashionStore() != null
                        ? Integer.toHexString(System.identityHashCode(getFashionStore()))
                        : "null");
    }
}
