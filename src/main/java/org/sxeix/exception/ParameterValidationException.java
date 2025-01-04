package org.sxeix.exception;

/**
 * Exception for parameter validation
 */
public class ParameterValidationException extends Exception {
    public ParameterValidationException(final String message) {
        super(message);
    }
}
