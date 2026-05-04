package com.topcv.forms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record FieldRequest(
        @NotBlank String label,
        @NotBlank String type,
        @NotNull Integer displayOrder,
        @NotNull Boolean required,
        List<OptionRequest> options
) {
}
