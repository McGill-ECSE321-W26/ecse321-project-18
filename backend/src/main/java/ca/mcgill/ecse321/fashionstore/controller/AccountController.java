package ca.mcgill.ecse321.fashionstore.controller;

import ca.mcgill.ecse321.fashionstore.dto.AccountRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.AccountResponseDto;
import ca.mcgill.ecse321.fashionstore.service.AccountService;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Controller for Account REST API Endpoints */
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
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Login check for accounts
     *
     * @param accountRequestDto A request Dto with account email and password
     * @return An AccountResponseDto with the id, email and account type
     * @author Flavie Qin
     */
    @PostMapping("/fashionstore/accout/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AccountResponseDto accountLogin(@RequestBody AccountRequestDto accountRequestDto) {
        return accountService.accountLoginCheck(accountRequestDto);
    }
}
