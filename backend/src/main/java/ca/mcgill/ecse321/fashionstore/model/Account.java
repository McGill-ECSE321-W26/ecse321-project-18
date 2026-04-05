/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.36.0.8183.32a6408a9 modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Transient;

// line 4 "../../../../../../model.ump"
// line 83 "../../../../../../model.ump"
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Account {

    // ------------------------
    // MEMBER VARIABLES
    // ------------------------

    // Account Attributes
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(unique = true)
    private String email;

    private String password;

    // Helper Variables
    @Transient private boolean canSetId;
    @Transient private boolean canSetEmail;

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public Account() {
        canSetId = true;
        canSetEmail = true;
        password = null;
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

    public int getId() {
        return id;
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
                + "id"
                + ":"
                + getId()
                + ","
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
