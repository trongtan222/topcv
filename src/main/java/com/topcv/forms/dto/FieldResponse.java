package com.topcv.forms.dto;

import java.util.List;

public record FieldResponse(
        Long id,
        String label,
        String type,
        Integer displayOrder,
        boolean required,
        List<OptionResponse> options
) {
}
