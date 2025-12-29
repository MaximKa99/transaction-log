package com.epam.exception;

public class FormulaNotFoundException extends RuntimeException {
    public static final String FORMULA_NOT_FOUND_TEMPLATE = "Formula with id %d does not exist";

    public FormulaNotFoundException(String message) {
        super(message);
    }
}
