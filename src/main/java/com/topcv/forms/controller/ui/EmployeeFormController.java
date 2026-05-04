package com.topcv.forms.controller.ui;

import com.topcv.forms.dto.SubmissionRequest;
import com.topcv.forms.dto.SubmissionValueRequest;
import com.topcv.forms.model.Form;
import com.topcv.forms.model.FormField;
import com.topcv.forms.service.FormService;
import com.topcv.forms.service.SubmissionService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/employee/forms")
public class EmployeeFormController {
    private final FormService formService;
    private final SubmissionService submissionService;

    public EmployeeFormController(FormService formService, SubmissionService submissionService) {
        this.formService = formService;
        this.submissionService = submissionService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("forms", formService.listActiveFormEntities());
        return "employee/forms";
    }

    @GetMapping("/{id}")
    public String fill(@PathVariable Long id, Model model) {
        Form form = formService.getFormEntity(id);
        model.addAttribute("form", form);
        return "employee/form-fill";
    }

    @PostMapping("/{id}")
    public String submit(@PathVariable Long id, Model model,
                         @RequestParam java.util.Map<String, String> params) {
        Form form = formService.getFormEntity(id);
        List<SubmissionValueRequest> values = new ArrayList<>();
        for (FormField field : form.getFields()) {
            String key = "field_" + field.getId();
            String value = params.get(key);
            if (value != null) {
                values.add(new SubmissionValueRequest(field.getId(), value));
            }
        }
        submissionService.submit(id, new SubmissionRequest(values));
        model.addAttribute("form", form);
        model.addAttribute("success", true);
        return "employee/form-fill";
    }
}
