package ca.mcgill.ecse321.fashionstore.controller;

import ca.mcgill.ecse321.fashionstore.service.UtilsService;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Utility controller */
@CrossOrigin
@RestController
@Profile("dev")
@SuppressFBWarnings("SPRING_ENDPOINT")
public class UtilsController {
    private UtilsService utilsService;

    /**
     * Constructor for UtilsController.
     *
     * @param utilsService Utils service class.
     * @author Cyrus Fung
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public UtilsController(UtilsService utilsService) {
        this.utilsService = utilsService;
    }

    /**
     * Create entries in database.
     *
     * @author Cyrus Fung
     */
    @PostMapping("/fashionstore/admin/rand")
    @ResponseStatus(HttpStatus.CREATED)
    public void createRandomData() {
        utilsService.generateRandomData();
    }

    /**
     * Create entries in database.
     *
     * @author Cyrus Fung
     */
    @PostMapping("/fashionstore/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public void createData() {
        utilsService.generateData();
    }

    /**
     * Delete all entries in database.
     *
     * @author Cyrus Fung
     */
    @DeleteMapping("/fashionstore/admin")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteData() {
        utilsService.deleteAllData();
    }
}
