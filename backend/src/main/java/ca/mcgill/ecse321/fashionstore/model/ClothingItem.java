/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.8043.819096d90 modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

// line 60 "../../../../../../model.ump"
// line 109 "../../../../../../model.ump"
public class ClothingItem {

    // ------------------------
    // ENUMERATIONS
    // ------------------------

    public enum Size {
        XS,
        S,
        M,
        L,
        XL
    }

    public enum Colour {
        RED,
        ORANGE,
        YELLOW,
        GREEN,
        BLUE,
        PURPLE,
        PINK,
        BLACK,
        GREY,
        WHITE,
        BROWN
    }

    // ------------------------
    // STATIC VARIABLES
    // ------------------------

    private static int nextId = 1;

    // ------------------------
    // MEMBER VARIABLES
    // ------------------------

    // ClothingItem Attributes
    private Size size;
    private Colour colour;
    private int numInStock;

    // Autounique Attributes
    private int id;

    // ClothingItem Associations
    private ClothingProduct clothingProduct;

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public ClothingItem(
            Size aSize, Colour aColour, int aNumInStock, ClothingProduct aClothingProduct) {
        size = aSize;
        colour = aColour;
        numInStock = aNumInStock;
        id = nextId++;
        boolean didAddClothingProduct = setClothingProduct(aClothingProduct);
        if (!didAddClothingProduct) {
            throw new RuntimeException(
                    "Unable to create item due to clothingProduct. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
        }
    }

    // ------------------------
    // INTERFACE
    // ------------------------

    public boolean setSize(Size aSize) {
        boolean wasSet = false;
        size = aSize;
        wasSet = true;
        return wasSet;
    }

    public boolean setColour(Colour aColour) {
        boolean wasSet = false;
        colour = aColour;
        wasSet = true;
        return wasSet;
    }

    public boolean setNumInStock(int aNumInStock) {
        boolean wasSet = false;
        numInStock = aNumInStock;
        wasSet = true;
        return wasSet;
    }

    public Size getSize() {
        return size;
    }

    public Colour getColour() {
        return colour;
    }

    public int getNumInStock() {
        return numInStock;
    }

    public int getId() {
        return id;
    }

    /* Code from template association_GetOne */
    public ClothingProduct getClothingProduct() {
        return clothingProduct;
    }

    /* Code from template association_SetOneToMany */
    public boolean setClothingProduct(ClothingProduct aClothingProduct) {
        boolean wasSet = false;
        if (aClothingProduct == null) {
            return wasSet;
        }

        ClothingProduct existingClothingProduct = clothingProduct;
        clothingProduct = aClothingProduct;
        if (existingClothingProduct != null && !existingClothingProduct.equals(aClothingProduct)) {
            existingClothingProduct.removeItem(this);
        }
        clothingProduct.addItem(this);
        wasSet = true;
        return wasSet;
    }

    public void delete() {
        ClothingProduct placeholderClothingProduct = clothingProduct;
        this.clothingProduct = null;
        if (placeholderClothingProduct != null) {
            placeholderClothingProduct.removeItem(this);
        }
    }

    public String toString() {
        return super.toString()
                + "["
                + "id"
                + ":"
                + getId()
                + ","
                + "numInStock"
                + ":"
                + getNumInStock()
                + "]"
                + System.getProperties().getProperty("line.separator")
                + "  "
                + "size"
                + "="
                + (getSize() != null
                        ? !getSize().equals(this)
                                ? getSize().toString().replaceAll("  ", "    ")
                                : "this"
                        : "null")
                + System.getProperties().getProperty("line.separator")
                + "  "
                + "colour"
                + "="
                + (getColour() != null
                        ? !getColour().equals(this)
                                ? getColour().toString().replaceAll("  ", "    ")
                                : "this"
                        : "null")
                + System.getProperties().getProperty("line.separator")
                + "  "
                + "clothingProduct = "
                + (getClothingProduct() != null
                        ? Integer.toHexString(System.identityHashCode(getClothingProduct()))
                        : "null");
    }
}
