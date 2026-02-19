/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8108.3ce48223a modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

// line 4 "../../../../../../model.ump"
// line 67 "../../../../../../model.ump"
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Account {

    // ------------------------
    // MEMBER VARIABLES
    // ------------------------

    // Account Attributes
    @Id private String email;
    private String password;

    // Helper Variables
    private boolean canSetEmail;

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public Account() {
        canSetEmail = true;
        password = null;
    }

    // ------------------------
    // INTERFACE
    // ------------------------
    /* Code from template attribute_SetImmutable */
    public boolean setEmail(String aEmail) {
        boolean wasSet = false;
        if (!canSetEmail) {
            return false;
        }
        canSetEmail = false;
        email = aEmail;
        wasSet = true;
        return wasSet;
    }

    public boolean setPassword(String aPassword) {
        boolean wasSet = false;
        password = aPassword;
        wasSet = true;
        return wasSet;
    }

    /** unique */
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void delete() {}

    public String toString() {
        return super.toString()
                + "["
                + "email"
                + ":"
                + getEmail()
                + ","
                + "password"
                + ":"
                + getPassword()
                + "]";
    }
}
