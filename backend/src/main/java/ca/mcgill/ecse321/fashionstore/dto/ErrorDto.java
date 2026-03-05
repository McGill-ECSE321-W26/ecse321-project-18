package ca.mcgill.ecse321.fashionstore.dto;

import java.util.List;

/** ErrorDTO class. */
public record ErrorDto(List<String> errors) {

    /** ErrorDTO compact constructor. */
    public ErrorDto {
        errors = (errors == null) ? List.of() : List.copyOf(errors);
    }

    /** ErrorDTO constructor that receives an error string. */
    public ErrorDto(String error) {
        this(List.of(error));
    }
}
