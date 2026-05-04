package com.topcv.forms.dto;

import java.time.LocalDateTime;

public record SubmissionResponse(
        Long id,
        LocalDateTime submittedAt
) {
}
