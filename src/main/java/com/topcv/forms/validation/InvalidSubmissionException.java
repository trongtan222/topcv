package com.topcv.forms.validation;

import java.util.List;

public class InvalidSubmissionException extends RuntimeException {
    private final List<FieldErrorDetail> errors;

    public InvalidSubmissionException(String message, List<FieldErrorDetail> errors) {
        super(message);
        this.errors = errors;
    }

    public List<FieldErrorDetail> getErrors() {
        return errors;
    }
}
