package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.OwnerRequestDto;
import ca.mcgill.ecse321.fashionstore.model.Owner;
import ca.mcgill.ecse321.fashionstore.repository.OwnerRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/** Owner Service class. */
@Service
@Validated
public class OwnerService {
    private final OwnerRepository ownerRepository;

    /**
     * OwnerService constructor.
     *
     * @param ownerRepository OwnerRepository required to access the database.
     * @author Cyrus Fung (cfung89)
     */
    @Autowired
    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    /**
     * Service method to update a owner's information.
     *
     * @param id ID of owner who is being updated.
     * @param ownerRequestDto Request DTO for owner.
     * @return The updated owner.
     * @author Cyrus Fung (cfung89)
     */
    @Transactional
    public Owner updateOwner(int id, @Valid OwnerRequestDto ownerRequestDto) {
        Owner owner = Utils.findOwnerById(ownerRepository, id);
        if (ownerRequestDto.password() != null && !ownerRequestDto.password().isBlank()) {
            owner.setPassword(ownerRequestDto.password());
        }
        return ownerRepository.save(owner);
    }
}
