package com.topcv.forms.service;

import com.topcv.forms.dto.SubmissionRequest;
import com.topcv.forms.dto.SubmissionResponse;
import com.topcv.forms.dto.SubmissionValueRequest;
import com.topcv.forms.model.Form;
import com.topcv.forms.model.FormField;
import com.topcv.forms.model.FormSubmission;
import com.topcv.forms.model.SubmissionValue;
import com.topcv.forms.repository.FormSubmissionRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.topcv.forms.validation.SubmissionValidator;

@Service
public class SubmissionService {
    private final FormSubmissionRepository submissionRepository;
    private final FormService formService;
    private final SubmissionValidator submissionValidator;

    public SubmissionService(FormSubmissionRepository submissionRepository, FormService formService,
                             SubmissionValidator submissionValidator) {
        this.submissionRepository = submissionRepository;
        this.formService = formService;
        this.submissionValidator = submissionValidator;
    }

    public SubmissionResponse submit(Long formId, SubmissionRequest request) {
        Form form = formService.getFormEntity(formId);
        Map<Long, String> submittedValues = new HashMap<>();
        for (SubmissionValueRequest valueRequest : request.values()) {
            submittedValues.put(valueRequest.fieldId(), valueRequest.value());
        }

        submissionValidator.validate(form, submittedValues);

        FormSubmission submission = new FormSubmission();
        submission.setForm(form);
        submission.setSubmittedAt(LocalDateTime.now());

        for (FormField field : form.getFields()) {
            String value = submittedValues.get(field.getId());
            if (value == null || value.isBlank()) {
                continue;
            }

            SubmissionValue submissionValue = new SubmissionValue();
            submissionValue.setSubmission(submission);
            submissionValue.setField(field);
            submissionValue.setValue(value);
            submission.getValues().add(submissionValue);
        }

        FormSubmission saved = submissionRepository.save(submission);
        return new SubmissionResponse(saved.getId(), saved.getSubmittedAt());
    }

    public List<FormSubmission> listSubmissions(Long formId) {
        return submissionRepository.findByFormIdOrderBySubmittedAtDesc(formId);
    }

    public Page<FormSubmission> listAllSubmissions(Pageable pageable, boolean includeValues) {
        if (includeValues) {
            return submissionRepository.findAllWithValuesByOrderBySubmittedAtDesc(pageable);
        }
        return submissionRepository.findAllByOrderBySubmittedAtDesc(pageable);
    }
}
