package com.topcv.forms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OptionRequest(
        @NotBlank String label,
        @NotBlank String value,
        @NotNull Integer displayOrder
) {
}
