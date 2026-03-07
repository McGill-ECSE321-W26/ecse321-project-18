package ca.mcgill.ecse321.fashionstore.controller;

import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for Account REST API Endpoints
 */
@RestController
@SuppressFBWarnings("SPRING_ENDPOINT")
public class AccountController {
    private AccountService accountService;

    /**
     * Constructor for AccountController class
     *
     * @param accountService account service class
     * @author Qiuyu Huang (redacted24)
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public AccountController(AccountService accoutService) {
        this.accountService = accoutService;
    }
}
