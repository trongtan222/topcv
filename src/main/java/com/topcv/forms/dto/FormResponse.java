package com.topcv.forms.dto;

import java.util.List;

public record FormResponse(
        Long id,
        String title,
        String description,
        Integer displayOrder,
        String status,
        List<FieldResponse> fields
) {
}
