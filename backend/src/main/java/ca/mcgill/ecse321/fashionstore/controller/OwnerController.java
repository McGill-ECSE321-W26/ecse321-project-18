package ca.mcgill.ecse321.fashionstore.controller;

import ca.mcgill.ecse321.fashionstore.dto.OwnerRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.OwnerResponseDto;
import ca.mcgill.ecse321.fashionstore.model.Owner;
import ca.mcgill.ecse321.fashionstore.service.OwnerService;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Controller for Owner REST API endpoints. */
@CrossOrigin
@RestController
@SuppressFBWarnings("SPRING_ENDPOINT")
public class OwnerController {
    private OwnerService ownerService;

    /**
     * Constructor for OwnerController.
     *
     * @param ownerService Owner service class.
     * @author Carolyn Wu (cw118)
     */
    @Autowired
    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    /**
     * Updates a owner's information.
     *
     * @param ownerId Owner ID.
     * @param ownerRequestDto DTO specifying the new information.
     * @return A DTO representing the owner with updated information.
     * @author Cyrus Fung (cfung89)
     */
    @PutMapping("/fashionstore/account/owner/{ownerId}")
    public OwnerResponseDto updateOwner(
            @PathVariable int ownerId, @RequestBody OwnerRequestDto ownerRequestDto) {
        Owner owner = ownerService.updateOwner(ownerId, ownerRequestDto);
        OwnerResponseDto dto = new OwnerResponseDto(owner);
        return dto;
    }
}
