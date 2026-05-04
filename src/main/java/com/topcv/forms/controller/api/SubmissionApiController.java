package com.topcv.forms.controller.api;

import com.topcv.forms.dto.SubmissionRequest;
import com.topcv.forms.dto.SubmissionResponse;
import com.topcv.forms.service.SubmissionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/forms/{formId}/submissions")
public class SubmissionApiController {
    private final SubmissionService submissionService;

    public SubmissionApiController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResponse submit(@PathVariable Long formId, @Valid @RequestBody SubmissionRequest request) {
        return submissionService.submit(formId, request);
    }

    @GetMapping
    public List<SubmissionResponse> list(@PathVariable Long formId) {
        return submissionService.listSubmissions(formId)
                .stream()
                .map(submission -> new SubmissionResponse(submission.getId(), submission.getSubmittedAt()))
                .toList();
    }
}
