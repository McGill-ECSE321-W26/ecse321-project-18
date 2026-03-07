package ca.mcgill.ecse321.fashionstore.service;

import ca.mcgill.ecse321.fashionstore.dto.AccountRequestDto;
import ca.mcgill.ecse321.fashionstore.dto.AccountResponseDto;
import ca.mcgill.ecse321.fashionstore.exception.FashionStoreException;
import ca.mcgill.ecse321.fashionstore.model.Account;
import ca.mcgill.ecse321.fashionstore.repository.AccountRepository;
import jakarta.validation.Valid;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/** Service class for AccountService. */
@Service
@Validated
public class AccountService {
    private AccountRepository accountRepository;

    /**
     * Constructor for AccountRepository class
     *
     * @param accountRepository account repository class
     * @author Qiuyu Huang (redacted24)
     */
    @Autowired
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Checks if email exists and the password matches. If both are valid, user is authenticated and
     * granted access to the system. Otherwise, user is denied access and an error message is shown.
     * @param requestDto
     * @return An AccoutResponseDTO with the id, email and the account type.
     * @throws FashionStoreException if an account with the email isn't found, or a password doesn't match
     * @author Qiuyu Huang (redacted24)
     */
    public AccountResponseDto accoutLoginCheck(@Valid AccountRequestDto requestDto) {
        Account account = accountRepository.findAccountByEmail(requestDto.email());
        // Email check
        if (account == null) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST,
                    "An account with that email does not exist."
                    );
        }

        // Password check
        if (!account.getPassword().equals(requestDto.password())) {
            throw new FashionStoreException(
                    HttpStatus.BAD_REQUEST,
                    "Password is incorrect."
            );
        }

        // Successful authentication by user
        return new AccountResponseDto(account.getId(), account.getEmail());
    }
}