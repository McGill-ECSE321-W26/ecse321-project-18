package ca.mcgill.ecse321.fashionstore.dto;

import java.util.List;

/**
 * ErrorDTO class.
 *
 * @author Cyrus Fung (cfung89)
 */
public record ErrorDto(List<String> errors) {

    /**
     * ErrorDTO compact constructor.
     *
     * @author Cyrus Fung (cfung89)
     */
    public ErrorDto {
        errors = (errors == null) ? List.of() : List.copyOf(errors);
    }

    /**
     * ErrorDTO constructor that receives an error string.
     *
     * @author Cyrus Fung (cfung89)
     */
    public ErrorDto(String error) {
        this(List.of(error));
    }
}
