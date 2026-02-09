/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.35.0.8043.819096d90 modeling language!*/

package ca.mcgill.ecse321.fashionstore.model;

import java.util.*;

// line 11 "../../../../../../model.ump"
// line 79 "../../../../../../model.ump"
public abstract class Account {

    // ------------------------
    // STATIC VARIABLES
    // ------------------------

    private static Map<String, Account> accountsByEmail = new HashMap<String, Account>();

    // ------------------------
    // MEMBER VARIABLES
    // ------------------------

    // Account Attributes
    private String email;
    private String password;

    // Helper Variables
    private boolean canSetEmail;

    // ------------------------
    // CONSTRUCTOR
    // ------------------------

    public Account(String aEmail, String aPassword) {
        canSetEmail = true;
        password = aPassword;
        if (!setEmail(aEmail)) {
            throw new RuntimeException(
                    "Cannot create due to duplicate email. See https://manual.umple.org?RE003ViolationofUniqueness.html");
        }
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
        String anOldEmail = getEmail();
        if (anOldEmail != null && anOldEmail.equals(aEmail)) {
            return true;
        }
        if (hasWithEmail(aEmail)) {
            return wasSet;
        }
        canSetEmail = false;
        email = aEmail;
        wasSet = true;
        if (anOldEmail != null) {
            accountsByEmail.remove(anOldEmail);
        }
        accountsByEmail.put(aEmail, this);
        return wasSet;
    }

    public boolean setPassword(String aPassword) {
        boolean wasSet = false;
        password = aPassword;
        wasSet = true;
        return wasSet;
    }

    public String getEmail() {
        return email;
    }

    /* Code from template attribute_GetUnique */
    public static Account getWithEmail(String aEmail) {
        return accountsByEmail.get(aEmail);
    }

    /* Code from template attribute_HasUnique */
    public static boolean hasWithEmail(String aEmail) {
        return getWithEmail(aEmail) != null;
    }

    public String getPassword() {
        return password;
    }

    public void delete() {
        accountsByEmail.remove(getEmail());
    }

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
