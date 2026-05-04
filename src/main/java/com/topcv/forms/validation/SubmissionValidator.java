package com.topcv.forms.validation;

import com.topcv.forms.model.FieldType;
import com.topcv.forms.model.Form;
import com.topcv.forms.model.FormField;
import com.topcv.forms.model.FormFieldOption;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class SubmissionValidator {
    private static final Pattern HEX_PATTERN = Pattern.compile("^#[0-9A-Fa-f]{6}$");
    private static final DateTimeFormatter ALT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void validate(Form form, Map<Long, String> values) {
        List<FieldErrorDetail> errors = new ArrayList<>();

        for (FormField field : form.getFields()) {
            String value = values.get(field.getId());
            if (field.isRequired() && (value == null || value.isBlank())) {
                errors.add(new FieldErrorDetail(field.getId(), field.getLabel(), "Field is required"));
                continue;
            }
            if (value == null || value.isBlank()) {
                continue;
            }

            validateValue(field, value, errors);
        }

        if (!errors.isEmpty()) {
            throw new InvalidSubmissionException("Submission validation failed", dedupeErrors(errors));
        }
    }

    private List<FieldErrorDetail> dedupeErrors(List<FieldErrorDetail> errors) {
        Map<String, FieldErrorDetail> unique = new LinkedHashMap<>();
        for (FieldErrorDetail error : errors) {
            String key = error.fieldId() + "|" + error.message();
            unique.putIfAbsent(key, error);
        }
        return new ArrayList<>(unique.values());
    }

    private void validateValue(FormField field, String value, List<FieldErrorDetail> errors) {
        FieldType type = field.getType();
        switch (type) {
            case TEXT -> {
                if (value.length() > 200) {
                    errors.add(new FieldErrorDetail(field.getId(), field.getLabel(), "Max length 200"));
                }
            }
            case NUMBER -> {
                BigDecimal number = parseNumber(value, field, errors);
                if (number != null && (number.compareTo(BigDecimal.ZERO) < 0
                        || number.compareTo(new BigDecimal("100")) > 0)) {
                    errors.add(new FieldErrorDetail(field.getId(), field.getLabel(), "Value must be 0-100"));
                }
            }
            case DATE -> {
                LocalDate date = parseDate(value, field, errors);
                if (date != null && date.isBefore(LocalDate.now())) {
                    errors.add(new FieldErrorDetail(field.getId(), field.getLabel(), "Date cannot be in the past"));
                }
            }
            case COLOR -> {
                if (!HEX_PATTERN.matcher(value).matches()) {
                    errors.add(new FieldErrorDetail(field.getId(), field.getLabel(), "Invalid hex color"));
                }
            }
            case SELECT -> {
                boolean match = false;
                for (FormFieldOption option : field.getOptions()) {
                    if (option.getValue().equals(value)) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    errors.add(new FieldErrorDetail(field.getId(), field.getLabel(), "Invalid option"));
                }
            }
            default -> errors.add(new FieldErrorDetail(field.getId(), field.getLabel(), "Unsupported field type"));
        }
    }

    private BigDecimal parseNumber(String value, FormField field, List<FieldErrorDetail> errors) {
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            errors.add(new FieldErrorDetail(field.getId(), field.getLabel(), "Invalid number"));
            return null;
        }
    }

    private LocalDate parseDate(String value, FormField field, List<FieldErrorDetail> errors) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            try {
                return LocalDate.parse(value, ALT_DATE_FORMAT);
            } catch (DateTimeParseException ignored) {
                errors.add(new FieldErrorDetail(field.getId(), field.getLabel(), "Invalid date"));
                return null;
            }
        }
    }
}
