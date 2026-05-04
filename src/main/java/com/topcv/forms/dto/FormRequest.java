package com.topcv.forms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record FormRequest(
        @NotBlank String title,
        String description,
        @NotNull Integer displayOrder,
        @NotBlank String status,
        List<FieldRequest> fields
) {
}
