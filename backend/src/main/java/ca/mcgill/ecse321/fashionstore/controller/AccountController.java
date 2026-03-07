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

/** Controller for Account REST API endpoints */
@RestController
@SuppressFBWarnings("SPRING_ENDPOINT")
public class AccountController {
    private AccountService accountService;

    /**
     * Constructor for AccountController.
     *
     * @param accountService Account service class.
     * @author Aurore Zhang (ororio0)
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
     * @author Qiuyu Huang (redacted24)
     */
    @PostMapping("/fashionstore/account/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AccountResponseDto accountLogin(@RequestBody AccountRequestDto accountRequestDto) {
        return accountService.accountLoginCheck(accountRequestDto);
    }

    /**
     * Creates a new employee account in the fashion store system.
     *
     * @param accountRequestDto AccountRequestDto (email, password).
     * @return Returns the new AccountResponseDto (id, email).
     * @author Aurore Zhang (ororio0)
     */
    @PostMapping("/fashionstore/account/employee")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponseDto createEmployeeAccount(
            @RequestBody AccountRequestDto accountRequestDto) {
        return accountService.createEmployeeAccount(accountRequestDto);
    }

    /**
     * Creates a new customer account in the fashion store system.
     *
     * @param accountRequestDto AccountRequestDto (email, password).
     * @return Returns the new AccountResponseDto (id, email).
     * @author Aurore Zhang (ororio0)
     */
    @PostMapping("/fashionstore/account/customer")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponseDto createCustomerAccount(
            @RequestBody AccountRequestDto accountRequestDto) {
        return accountService.createCustomerAccount(accountRequestDto);
    }
}
