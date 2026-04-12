package ca.mcgill.ecse321.fashionstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.fashionstore.dto.OwnerRequestDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Owner;
import ca.mcgill.ecse321.fashionstore.repository.OwnerRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;

/** Owner Service class tests. */
@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class OwnerServiceTests {
    @Mock private OwnerRepository ownerRepository;

    @InjectMocks OwnerService ownerService;

    private static final int OWNER_ID = 1;
    private static final String OWNER_EMAIL = "owner@fashionstore.com";
    private static final String OWNER_PASSWORD = "owner12345";
    private static final String OWNER_UPDATED_PASSWORD = "updated12345";

    private Owner owner;

    /** Setup function for Owner service layer tests. */
    @BeforeEach
    void setup() {
        owner = createOwner(OWNER_ID, OWNER_EMAIL, OWNER_PASSWORD);
    }

    @AfterEach
    void clearDatabase() {
        ownerRepository.deleteAll();
    }

    /**
     * Helper for service layer test for updating an owner.
     *
     * @author Aurore Zhang (ororio0)
     */
    private Owner updateOwnerSetup() {
        // Arrange
        when(ownerRepository.findById(OWNER_ID)).thenReturn(Optional.of(owner));
        when(ownerRepository.save(any(Owner.class)))
                .thenAnswer((InvocationOnMock invocation) -> invocation.getArgument(0));

        // Act
        OwnerRequestDto updatedOwner = new OwnerRequestDto(OWNER_EMAIL, OWNER_UPDATED_PASSWORD);
        Owner owner = ownerService.updateOwner(OWNER_ID, updatedOwner);

        return owner;
    }

    private void verifyUpdateOwnerByValidId() {
        verify(ownerRepository, times(1))
                .save(
                        argThat(
                                savedOwner ->
                                        OWNER_UPDATED_PASSWORD.equals(savedOwner.getPassword())));
    }

    /**
     * Service layer test for updating an owner by a valid ID.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void updateOwnerByValidIdSuccess() {
        // Arrange and act
        Owner owner = updateOwnerSetup();

        // Assert
        assertNotNull(owner, "Owner is null.");
        assertEquals(OWNER_EMAIL, owner.getEmail(), "Owner does not contain correct email.");
        assertEquals(
                OWNER_UPDATED_PASSWORD,
                owner.getPassword(),
                "Owner does not contain correct password.");
        verifyUpdateOwnerByValidId();
    }

    /**
     * Service layer test for updating a shopping cart items with invalid ID.
     *
     * @author Aurore Zhang (ororio0)
     */
    @Test
    void updateOwnerByInvalidIdFail() {
        int id = owner.getId() + 28;
        when(ownerRepository.findById(id)).thenReturn(Optional.empty());
        OwnerRequestDto updatedOwner = new OwnerRequestDto(OWNER_EMAIL, OWNER_UPDATED_PASSWORD);

        assertThrows(
                FashionStoreException.class,
                () -> ownerService.updateOwner(id, updatedOwner),
                "Trying to find non existent owner ID should not find anything.");
    }

    private Owner createOwner(int id, String email, String password) {
        Owner newOwner = new Owner();
        newOwner.setId(id);
        newOwner.setEmail(email);
        newOwner.setPassword(password);
        return newOwner;
    }
}