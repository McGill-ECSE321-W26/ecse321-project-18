package ca.mcgill.ecse321.fashionstore.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.mcgill.ecse321.fashionstore.model.Owner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * {@summary Test suite for owner (account) persistence in the database.}
 *
 * @author Carolyn Wu (cw118)
 */
@SpringBootTest
class OwnerRepositoryTests {
    @Autowired private OwnerRepository ownerRepository;
    private Owner owner;

    /**
     * Creates and saves an owner before each test.
     *
     * @author Carolyn Wu (cw118)
     */
    @BeforeEach
    void createAndSaveOwner() {
        // create Owner object
        String email = "owner@fashionstore.com";
        String password = "averysafepassword";

        Owner newOwner = new Owner();
        newOwner.setEmail(email);
        newOwner.setPassword(password);

        // save the owner
        ownerRepository.save(newOwner);
        this.owner = newOwner;
    }

    /**
     * Clears database after each test.
     *
     * @author Carolyn Wu (cw118)
     */
    @AfterEach
    void clearDatabase() {
        ownerRepository.deleteAll();
    }

    /**
     * Test retrieval of persisted owner from database: should not be null.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void testPersistAndLoadOwner() {
        // read owner from database
        Owner ownerFromDb = ownerRepository.findOwnerByEmail(owner.getEmail());

        // assert that owner was found
        assertNotNull(ownerFromDb, "Could not find saved owner in the database");
    }

    /**
     * Test read (retrieval) of owner email.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void testReadOwnerEmail() {
        // get owner from database
        Owner ownerFromDb = ownerRepository.findOwnerByEmail(owner.getEmail());

        // check that email was persisted
        assertEquals(
                owner.getEmail(), ownerFromDb.getEmail(), "Owner email is not saved in database");
    }

    /**
     * Test read (retrieval) of owner password.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void testReadOwnerPassword() {
        // get owner from database
        Owner ownerFromDb = ownerRepository.findOwnerByEmail(owner.getEmail());

        // check that email was persisted
        assertEquals(
                owner.getPassword(),
                ownerFromDb.getPassword(),
                "Owner password is not saved in database");
    }

    /**
     * Test write of owner password. Note that email write is not tested because email is the
     * primary key, making it a unique and immutable attribute.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void testWriteOwnerPassword() {
        // get owner from database
        Owner ownerFromDb = ownerRepository.findOwnerByEmail(owner.getEmail());

        // update the password
        String newPassword = "asaferpasswordwooo";
        ownerFromDb.setPassword(newPassword);
        ownerRepository.save(ownerFromDb);

        // check that new password was persisted
        Owner updatedOwnerFromDb = ownerRepository.findOwnerByEmail(owner.getEmail());
        assertEquals(
                newPassword,
                updatedOwnerFromDb.getPassword(),
                "Owner password update was not persisted");
    }

    /**
     * Test deleting the owner.
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void testDeleteOwner() {
        // get owner from database
        String ownerEmail = owner.getEmail();
        Owner ownerFromDb = ownerRepository.findOwnerByEmail(ownerEmail);

        // delete the owner from the database
        ownerRepository.delete(ownerFromDb);

        // check that we can no longer find an owner with that email
        Owner updatedOwnerFromDb = ownerRepository.findOwnerByEmail(ownerEmail);
        assertNull(updatedOwnerFromDb, "Owner deletion was not persisted");
    }

    /**
     * Test deleting the owner by id (email).
     *
     * @author Carolyn Wu (cw118)
     */
    @Test
    void testDeleteOwnerById() {
        // get owner from database
        String ownerEmail = owner.getEmail();

        // delete the owner using its id (email) from the database
        ownerRepository.deleteById(ownerEmail);

        // check that we can no longer find an owner with that email
        Owner updatedOwnerFromDb = ownerRepository.findOwnerByEmail(ownerEmail);
        assertNull(updatedOwnerFromDb, "Owner deletion by id was not persisted");
    }
}
