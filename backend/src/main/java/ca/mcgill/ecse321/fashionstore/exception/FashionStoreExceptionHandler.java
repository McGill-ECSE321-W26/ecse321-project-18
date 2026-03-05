package ca.mcgill.ecse321.fashionstore.exception;

import ca.mcgill.ecse321.fashionstore.dto.ErrorDto;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Global exception handler. Intercepts exceptions thrown by Service or Controller layers. */
@ControllerAdvice
public class FashionStoreExceptionHandler {

    /**
     * Handles validation errors thrown by the Jakarta Validation API (entity or DTO constraints are
     * violated).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> handleConstraintViolationException(
            ConstraintViolationException e) {
        List<String> errors = new ArrayList<>();
        for (var error : e.getConstraintViolations()) {
            errors.add(error.getMessage());
        }
        return new ResponseEntity<>(new ErrorDto(errors), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles custom business logic exceptions. Extracts custom message and specific HttpStatus
     * defined when the exception was thrown in the Service layer.
     */
    @ExceptionHandler(FashionStoreException.class)
    public ResponseEntity<ErrorDto> handleEventRegistrationException(FashionStoreException e) {
        return new ResponseEntity<>(new ErrorDto(e.getMessage()), e.getStatus());
    }
}
