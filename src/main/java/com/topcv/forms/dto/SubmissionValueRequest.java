package com.topcv.forms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubmissionValueRequest(
        @NotNull Long fieldId,
        @NotBlank String value
) {
}
