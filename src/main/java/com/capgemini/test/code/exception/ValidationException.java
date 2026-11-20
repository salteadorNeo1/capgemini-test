package com.capgemini.test.code.exception;

public class ValidationException extends RuntimeException {
    private final String field;

    public ValidationException(String field) {
        super("error validation " + field);
        this.field = field;
    }

    public String getFormattedMessage() {
        return "error validation " + field;
    }
}
