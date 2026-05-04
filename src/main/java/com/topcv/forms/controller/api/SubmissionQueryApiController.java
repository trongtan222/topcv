package com.topcv.forms.controller.api;

import com.topcv.forms.dto.PageResponse;
import com.topcv.forms.dto.SubmissionFieldValueResponse;
import com.topcv.forms.dto.SubmissionListResponse;
import com.topcv.forms.service.SubmissionService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionQueryApiController {
    private final SubmissionService submissionService;

    public SubmissionQueryApiController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @GetMapping
        public PageResponse<SubmissionListResponse> list(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "20") int size,
                                 @RequestParam(defaultValue = "false") boolean includeValues) {
        Page<SubmissionListResponse> result = submissionService
            .listAllSubmissions(PageRequest.of(page, size), includeValues)
            .map(submission -> toResponse(submission, includeValues));
        return new PageResponse<>(result.getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

        private SubmissionListResponse toResponse(com.topcv.forms.model.FormSubmission submission, boolean includeValues) {
        List<SubmissionFieldValueResponse> values = null;
        if (includeValues) {
            values = submission.getValues()
                .stream()
                .map(value -> new SubmissionFieldValueResponse(value.getField().getId(), value.getValue()))
                .toList();
        }
        return new SubmissionListResponse(
            submission.getId(),
            submission.getForm().getId(),
            submission.getSubmittedAt(),
            values
        );
        }
}
