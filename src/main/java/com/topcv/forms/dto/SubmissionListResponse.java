package com.topcv.forms.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SubmissionListResponse(
        Long id,
        Long formId,
        LocalDateTime submittedAt,
        List<SubmissionFieldValueResponse> values
) {
}
