package ca.mcgill.ecse321.fashionstore.exception;

import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;

/**
 * Global exception to be thrown by the Service layer. Extend RuntimeException to make this an
 * unchecked exception.
 */
public class FashionStoreException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private HttpStatus status;

    /** FashionStoreException constructor */
    public FashionStoreException(@NonNull HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
