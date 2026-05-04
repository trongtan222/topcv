package com.topcv.forms.dto;

public record OptionResponse(
        Long id,
        String label,
        String value,
        Integer displayOrder
) {
}
