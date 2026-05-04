package com.topcv.forms.service;

import com.topcv.forms.dto.FieldRequest;
import com.topcv.forms.dto.FieldResponse;
import com.topcv.forms.dto.FormRequest;
import com.topcv.forms.dto.FormResponse;
import com.topcv.forms.dto.OptionRequest;
import com.topcv.forms.dto.OptionResponse;
import com.topcv.forms.model.FieldType;
import com.topcv.forms.model.Form;
import com.topcv.forms.model.FormField;
import com.topcv.forms.model.FormFieldOption;
import com.topcv.forms.model.FormStatus;
import com.topcv.forms.repository.FormRepository;
import com.topcv.forms.repository.FormSubmissionRepository;
import com.topcv.forms.repository.SubmissionValueRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FormService {
    private final FormRepository formRepository;
    private final FormSubmissionRepository formSubmissionRepository;
    private final SubmissionValueRepository submissionValueRepository;

    public FormService(FormRepository formRepository,
                       FormSubmissionRepository formSubmissionRepository,
                       SubmissionValueRepository submissionValueRepository) {
        this.formRepository = formRepository;
        this.formSubmissionRepository = formSubmissionRepository;
        this.submissionValueRepository = submissionValueRepository;
    }

    public List<FormResponse> listForms() {
        return formRepository.findAllByOrderByDisplayOrderAscIdAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<Form> listFormEntities() {
        return formRepository.findAllByOrderByDisplayOrderAscIdAsc();
    }

    public Page<FormResponse> listForms(Pageable pageable) {
        return formRepository.findAllByOrderByDisplayOrderAscIdAsc(pageable)
                .map(this::toResponse);
    }

    public List<Form> listActiveFormEntities() {
        return formRepository.findByStatusOrderByDisplayOrderAscIdAsc(FormStatus.ACTIVE);
    }

    public List<FormResponse> listActiveForms() {
        return formRepository.findByStatusOrderByDisplayOrderAscIdAsc(FormStatus.ACTIVE)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public FormResponse getForm(Long id) {
        Form form = formRepository.findWithFieldsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Form not found"));
        return toResponse(form);
    }

    public Form getFormEntity(Long id) {
        Form form = formRepository.findWithFieldsById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Form not found"));
        if (cleanupDuplicates(form)) {
            formRepository.save(form);
        }
        return form;
    }

    public FormResponse create(FormRequest request) {
        Form form = new Form();
        applyFormRequest(form, request);
        Form saved = formRepository.save(form);
        return toResponse(saved);
    }

    public FormResponse update(Long id, FormRequest request) {
        Form form = getFormEntity(id);
        applyFormRequest(form, request);
        Form saved = formRepository.save(form);
        return toResponse(saved);
    }

    public void delete(Long id) {
        if (!formRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Form not found");
        }
        if (formSubmissionRepository.existsByFormId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete form with submissions");
        }
        formRepository.deleteById(id);
    }

    public FieldResponse addField(Long formId, FieldRequest request) {
        Form form = getFormEntity(formId);
        ensureNoDuplicateField(form, request, null);
        FormField field = buildFieldFromRequest(request);
        field.setForm(form);
        form.getFields().add(field);
        formRepository.save(form);
        return toFieldResponse(field);
    }

    public FieldResponse updateField(Long formId, Long fieldId, FieldRequest request) {
        Form form = getFormEntity(formId);
        ensureNoDuplicateField(form, request, fieldId);
        FormField field = findField(form, fieldId);
        field.setLabel(request.label());
        field.setType(FieldType.valueOf(request.type().toUpperCase()));
        field.setDisplayOrder(request.displayOrder());
        field.setRequired(Boolean.TRUE.equals(request.required()));
        field.getOptions().clear();

        List<OptionRequest> options = request.options() == null ? List.of() : request.options();
        for (OptionRequest optionRequest : options) {
            FormFieldOption option = new FormFieldOption();
            option.setLabel(optionRequest.label());
            option.setValue(optionRequest.value());
            option.setDisplayOrder(optionRequest.displayOrder());
            option.setField(field);
            field.getOptions().add(option);
        }

        formRepository.save(form);
        return toFieldResponse(field);
    }

    public void deleteField(Long formId, Long fieldId) {
        Form form = getFormEntity(formId);
        FormField field = findField(form, fieldId);
        if (submissionValueRepository.existsByFieldId(fieldId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot delete field with submissions");
        }
        form.getFields().remove(field);
        formRepository.save(form);
    }

    private void applyFormRequest(Form form, FormRequest request) {
        form.setTitle(request.title());
        form.setDescription(request.description());
        form.setDisplayOrder(request.displayOrder());
        form.setStatus(FormStatus.valueOf(request.status().toUpperCase()));

        form.getFields().clear();
        List<FieldRequest> fields = request.fields() == null ? List.of() : request.fields();
        ensureNoDuplicateFields(fields);
        for (FieldRequest fieldRequest : fields) {
            FormField field = buildFieldFromRequest(fieldRequest);
            field.setForm(form);
            form.getFields().add(field);
        }
    }

    private FormResponse toResponse(Form form) {
        List<FieldResponse> fields = form.getFields().stream()
            .map(this::toFieldResponse)
            .toList();

        return new FormResponse(
                form.getId(),
                form.getTitle(),
                form.getDescription(),
                form.getDisplayOrder(),
                form.getStatus().name().toLowerCase(),
                fields
        );
    }

    private FormField buildFieldFromRequest(FieldRequest fieldRequest) {
        FormField field = new FormField();
        field.setLabel(fieldRequest.label());
        field.setType(FieldType.valueOf(fieldRequest.type().toUpperCase()));
        field.setDisplayOrder(fieldRequest.displayOrder());
        field.setRequired(Boolean.TRUE.equals(fieldRequest.required()));

        List<OptionRequest> options = fieldRequest.options() == null ? List.of() : fieldRequest.options();
        Set<String> seenValues = new LinkedHashSet<>();
        for (OptionRequest optionRequest : options) {
            if (!seenValues.add(optionRequest.value())) {
                continue;
            }
            FormFieldOption option = new FormFieldOption();
            option.setLabel(optionRequest.label());
            option.setValue(optionRequest.value());
            option.setDisplayOrder(optionRequest.displayOrder());
            option.setField(field);
            field.getOptions().add(option);
        }

        return field;
    }

    private FormField findField(Form form, Long fieldId) {
        return form.getFields().stream()
                .filter(field -> field.getId().equals(fieldId))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Field not found"));
    }

    private void ensureNoDuplicateFields(List<FieldRequest> fields) {
        Map<String, FieldRequest> seen = new LinkedHashMap<>();
        for (FieldRequest field : fields) {
            String key = fieldKey(field.label(), field.type(), field.displayOrder());
            if (seen.containsKey(key)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate field: " + field.label());
            }
            seen.put(key, field);
        }
    }

    private void ensureNoDuplicateField(Form form, FieldRequest request, Long ignoreFieldId) {
        String targetKey = fieldKey(request.label(), request.type(), request.displayOrder());
        for (FormField existing : form.getFields()) {
            if (ignoreFieldId != null && ignoreFieldId.equals(existing.getId())) {
                continue;
            }
            String existingKey = fieldKey(existing.getLabel(), existing.getType().name(), existing.getDisplayOrder());
            if (existingKey.equals(targetKey)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate field: " + existing.getLabel());
            }
        }
    }

    private String fieldKey(String label, String type, Integer displayOrder) {
        String safeLabel = label == null ? "" : label.trim().toLowerCase();
        String safeType = type == null ? "" : type.trim().toLowerCase();
        String safeOrder = displayOrder == null ? "" : displayOrder.toString();
        return safeLabel + "|" + safeType + "|" + safeOrder;
    }

    private boolean cleanupDuplicates(Form form) {
        boolean changed = false;
        Map<String, FormField> unique = new LinkedHashMap<>();
        for (FormField field : form.getFields()) {
            String key = fieldKey(field.getLabel(), field.getType().name(), field.getDisplayOrder());
            if (unique.containsKey(key)) {
                changed = true;
                continue;
            }
            unique.put(key, field);
            if (dedupeOptions(field)) {
                changed = true;
            }
        }

        if (unique.size() != form.getFields().size()) {
            form.setFields(new ArrayList<>(unique.values()));
            for (FormField field : form.getFields()) {
                field.setForm(form);
            }
        }

        return changed;
    }

    private boolean dedupeOptions(FormField field) {
        Map<String, FormFieldOption> unique = new LinkedHashMap<>();
        for (FormFieldOption option : field.getOptions()) {
            String key = option.getValue() == null ? "" : option.getValue().trim().toLowerCase();
            if (!unique.containsKey(key)) {
                unique.put(key, option);
            }
        }
        if (unique.size() != field.getOptions().size()) {
            field.setOptions(new LinkedHashSet<>(unique.values()));
            for (FormFieldOption option : field.getOptions()) {
                option.setField(field);
            }
            return true;
        }
        return false;
    }

    private FieldResponse toFieldResponse(FormField field) {
        List<OptionResponse> options = field.getOptions()
                .stream()
                .map(option -> new OptionResponse(option.getId(), option.getLabel(), option.getValue(), option.getDisplayOrder()))
                .toList();
        return new FieldResponse(
                field.getId(),
                field.getLabel(),
                field.getType().name().toLowerCase(),
                field.getDisplayOrder(),
                field.isRequired(),
                options
        );
    }
}
