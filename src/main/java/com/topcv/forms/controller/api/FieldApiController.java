package com.topcv.forms.controller.api;

import com.topcv.forms.dto.FieldRequest;
import com.topcv.forms.dto.FieldResponse;
import com.topcv.forms.service.FormService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/forms/{formId}/fields")
public class FieldApiController {
    private final FormService formService;

    public FieldApiController(FormService formService) {
        this.formService = formService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FieldResponse add(@PathVariable Long formId, @Valid @RequestBody FieldRequest request) {
        return formService.addField(formId, request);
    }

    @PutMapping("/{fieldId}")
    public FieldResponse update(@PathVariable Long formId, @PathVariable Long fieldId,
                                @Valid @RequestBody FieldRequest request) {
        return formService.updateField(formId, fieldId, request);
    }

    @DeleteMapping("/{fieldId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long formId, @PathVariable Long fieldId) {
        formService.deleteField(formId, fieldId);
    }
}
