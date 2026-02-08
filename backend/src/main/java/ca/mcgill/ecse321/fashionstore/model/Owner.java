/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.8043.819096d90 modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

import java.util.*;

// line 18 "../../../../../../model.ump"
// line 84 "../../../../../../model.ump"
public class Owner extends Account {

    // ------------------------
    // MEMBER VARIABLES
    // ------------------------

    // Owner Associations
    private FashionStore fashionStore;

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public Owner(String aEmail, String aPassword, FashionStore aFashionStore) {
        super(aEmail, aPassword);
        boolean didAddFashionStore = setFashionStore(aFashionStore);
        if (!didAddFashionStore) {
            throw new RuntimeException(
                    "Unable to create owner due to fashionStore. See https://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
        }
    }

    // ------------------------
    // INTERFACE
    // ------------------------
    /* Code from template association_GetOne */
    public FashionStore getFashionStore() {
        return fashionStore;
    }

    /* Code from template association_SetOneToOptionalOne */
    public boolean setFashionStore(FashionStore aNewFashionStore) {
        boolean wasSet = false;
        if (aNewFashionStore == null) {
            // Unable to setFashionStore to null, as owner must always be associated to a
            // fashionStore
            return wasSet;
        }

        Owner existingOwner = aNewFashionStore.getOwner();
        if (existingOwner != null && !equals(existingOwner)) {
            // Unable to setFashionStore, the current fashionStore already has a owner, which would
            // be orphaned if it were re-assigned
            return wasSet;
        }

        FashionStore anOldFashionStore = fashionStore;
        fashionStore = aNewFashionStore;
        fashionStore.setOwner(this);

        if (anOldFashionStore != null) {
            anOldFashionStore.setOwner(null);
        }
        wasSet = true;
        return wasSet;
    }

    public void delete() {
        FashionStore existingFashionStore = fashionStore;
        fashionStore = null;
        if (existingFashionStore != null) {
            existingFashionStore.setOwner(null);
        }
        super.delete();
    }
}
