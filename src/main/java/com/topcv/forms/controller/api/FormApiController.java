package com.topcv.forms.controller.api;

import com.topcv.forms.dto.FormRequest;
import com.topcv.forms.dto.FormResponse;
import com.topcv.forms.dto.PageResponse;
import com.topcv.forms.service.FormService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/forms")
public class FormApiController {
    private final FormService formService;

    public FormApiController(FormService formService) {
        this.formService = formService;
    }

    @GetMapping
    public Object list(@RequestParam(required = false) Integer page,
                       @RequestParam(required = false) Integer size) {
        if (page == null || size == null) {
            return formService.listForms();
        }
        Page<FormResponse> result = formService.listForms(PageRequest.of(page, size));
        return new PageResponse<>(result.getContent(), result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    @GetMapping("/active")
    public List<FormResponse> listActive() {
        return formService.listActiveForms();
    }

    @GetMapping("/{id}")
    public FormResponse get(@PathVariable Long id) {
        return formService.getForm(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FormResponse create(@Valid @RequestBody FormRequest request) {
        return formService.create(request);
    }

    @PutMapping("/{id}")
    public FormResponse update(@PathVariable Long id, @Valid @RequestBody FormRequest request) {
        return formService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        formService.delete(id);
    }
}
