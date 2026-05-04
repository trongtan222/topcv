package com.topcv.forms.validation;

public record FieldErrorDetail(
        Long fieldId,
        String label,
        String message
) {
}
