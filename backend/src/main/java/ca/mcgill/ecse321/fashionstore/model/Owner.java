/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8091.03bcab5b3 modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

import jakarta.persistence.Entity;

// line 11 "../../../../../../model.ump"
// line 72 "../../../../../../model.ump"
@Entity
public class Owner extends Account {

    // ------------------------
    // MEMBER VARIABLES
    // ------------------------

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public Owner(String aEmail, String aPassword) {
        super(aEmail, aPassword);
    }

    // ------------------------
    // INTERFACE
    // ------------------------

    public void delete() {
        super.delete();
    }
}
