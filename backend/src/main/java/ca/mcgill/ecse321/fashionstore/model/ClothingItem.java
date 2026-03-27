/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8183.32a6408a9 modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

// line 69 "../../../../../../model.ump"
// line 123 "../../../../../../model.ump"
@Entity
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
        @JsonProperty("Red")
        RED,
        @JsonProperty("Orange")
        ORANGE,
        @JsonProperty("Yellow")
        YELLOW,
        @JsonProperty("Green")
        GREEN,
        @JsonProperty("Blue")
        BLUE,
        @JsonProperty("Purple")
        PURPLE,
        @JsonProperty("Pink")
        PINK,
        @JsonProperty("Black")
        BLACK,
        @JsonProperty("Grey")
        GREY,
        @JsonProperty("White")
        WHITE,
        @JsonProperty("Brown")
        BROWN
    }

    // ------------------------
    // MEMBER VARIABLES
    // ------------------------

    // ClothingItem Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Enumerated(EnumType.STRING)
    private Size size;

    @Enumerated(EnumType.STRING)
    private Colour colour;

    private int numInStock;

    // ClothingItem Associations
    @ManyToOne private ClothingProduct clothingProduct;

    // Helper Variables
    private boolean canSetId;

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public ClothingItem() {
        canSetId = true;
        numInStock = 0;
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

    public int getId() {
        return id;
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

    /* Code from template association_GetOne */
    public ClothingProduct getClothingProduct() {
        return clothingProduct;
    }

    public boolean hasClothingProduct() {
        boolean has = clothingProduct != null;
        return has;
    }

    /* Code from template association_SetOptionalOneToMany */
    public boolean setClothingProduct(ClothingProduct aClothingProduct) {
        boolean wasSet = false;
        ClothingProduct existingClothingProduct = clothingProduct;
        clothingProduct = aClothingProduct;
        if (existingClothingProduct != null && !existingClothingProduct.equals(aClothingProduct)) {
            existingClothingProduct.removeItem(this);
        }
        if (aClothingProduct != null) {
            aClothingProduct.addItem(this);
        }
        wasSet = true;
        return wasSet;
    }

    public void delete() {
        if (clothingProduct != null) {
            ClothingProduct placeholderClothingProduct = clothingProduct;
            this.clothingProduct = null;
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
