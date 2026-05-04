package com.topcv.forms.controller.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.topcv.forms.dto.FieldRequest;
import com.topcv.forms.dto.FormRequest;
import com.topcv.forms.model.Form;
import com.topcv.forms.service.FormService;
import jakarta.validation.ValidationException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/admin/forms")
public class AdminFormController {
    private final FormService formService;
    private final ObjectMapper objectMapper;

    public AdminFormController(FormService formService, ObjectMapper objectMapper) {
        this.formService = formService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("forms", formService.listFormEntities());
        return "admin/forms";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new Form());
        model.addAttribute("fieldsJson", "[]");
        return "admin/form-edit";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Form form = formService.getFormEntity(id);
        model.addAttribute("form", form);
        model.addAttribute("fieldsJson", toFieldsJson(form));
        return "admin/form-edit";
    }

    @PostMapping
    public String save(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam Integer displayOrder,
            @RequestParam String status,
            @RequestParam String fieldsJson,
            Model model
    ) {
        try {
            FormRequest request = new FormRequest(title, description, displayOrder, status, parseFields(fieldsJson));
            formService.create(request);
            return "redirect:/admin/forms";
        } catch (ResponseStatusException ex) {
            return renderErrorForm(model, null, title, description, displayOrder, status, fieldsJson, ex);
        }
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam Integer displayOrder,
            @RequestParam String status,
            @RequestParam String fieldsJson,
            Model model
    ) {
        try {
            FormRequest request = new FormRequest(title, description, displayOrder, status, parseFields(fieldsJson));
            formService.update(id, request);
            return "redirect:/admin/forms";
        } catch (ResponseStatusException ex) {
            return renderErrorForm(model, id, title, description, displayOrder, status, fieldsJson, ex);
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        try {
            formService.delete(id);
            return "redirect:/admin/forms";
        } catch (ResponseStatusException ex) {
            return renderListError(ex.getReason());
        }
    }

    private String renderListError(String message) {
        return "redirect:/admin/forms?error=" + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
    }

    private List<FieldRequest> parseFields(String fieldsJson) {
        if (fieldsJson == null || fieldsJson.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(fieldsJson, new TypeReference<List<FieldRequest>>() {});
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid fields JSON");
        }
    }

    private String toFieldsJson(Form form) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                    form.getFields().stream().map(field -> new FieldRequest(
                            field.getLabel(),
                            field.getType().name().toLowerCase(),
                            field.getDisplayOrder(),
                            field.isRequired(),
                            field.getOptions().stream().map(option ->
                                    new com.topcv.forms.dto.OptionRequest(
                                            option.getLabel(),
                                            option.getValue(),
                                            option.getDisplayOrder()
                                    )
                            ).toList()
                    )).toList()
            );
        } catch (Exception ex) {
            throw new ValidationException("Failed to render fields JSON");
        }
    }

    private String renderErrorForm(Model model, Long id, String title, String description,
                                   Integer displayOrder, String status, String fieldsJson,
                                   ResponseStatusException ex) {
        Form form = new Form();
        form.setId(id);
        form.setTitle(title);
        form.setDescription(description);
        form.setDisplayOrder(displayOrder);
        if (status != null) {
            try {
                form.setStatus(com.topcv.forms.model.FormStatus.valueOf(status.toUpperCase()));
            } catch (IllegalArgumentException ignore) {
                form.setStatus(com.topcv.forms.model.FormStatus.DRAFT);
            }
        }

        model.addAttribute("form", form);
        model.addAttribute("fieldsJson", fieldsJson == null ? "[]" : fieldsJson);
        model.addAttribute("error", ex.getReason());
        return "admin/form-edit";
    }
}
