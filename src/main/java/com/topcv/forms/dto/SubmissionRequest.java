package com.topcv.forms.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SubmissionRequest(
        @NotNull List<SubmissionValueRequest> values
) {
}
