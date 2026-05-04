package com.topcv.forms.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.topcv.forms.model.FieldType;
import com.topcv.forms.model.Form;
import com.topcv.forms.model.FormField;
import com.topcv.forms.model.FormFieldOption;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubmissionValidatorTest {
    private SubmissionValidator validator;
    private Form form;

    @BeforeEach
    void setUp() {
        validator = new SubmissionValidator();
        form = new Form();
    }

    @Test
    void rejectsMissingRequiredField() {
        FormField field = field(1L, "Name", FieldType.TEXT, true);
        form.setFields(List.of(field));

        Map<Long, String> values = new HashMap<>();

        assertThrows(InvalidSubmissionException.class, () -> validator.validate(form, values));
    }

    @Test
    void rejectsTextTooLong() {
        FormField field = field(2L, "Bio", FieldType.TEXT, false);
        form.setFields(List.of(field));

        Map<Long, String> values = new HashMap<>();
        values.put(2L, "x".repeat(201));

        assertThrows(InvalidSubmissionException.class, () -> validator.validate(form, values));
    }

    @Test
    void rejectsInvalidNumber() {
        FormField field = field(3L, "Score", FieldType.NUMBER, false);
        form.setFields(List.of(field));

        Map<Long, String> values = new HashMap<>();
        values.put(3L, "abc");

        assertThrows(InvalidSubmissionException.class, () -> validator.validate(form, values));
    }

    @Test
    void rejectsNumberOutOfRange() {
        FormField field = field(4L, "Score", FieldType.NUMBER, false);
        form.setFields(List.of(field));

        Map<Long, String> values = new HashMap<>();
        values.put(4L, "101");

        assertThrows(InvalidSubmissionException.class, () -> validator.validate(form, values));
    }

    @Test
    void rejectsPastDate() {
        FormField field = field(5L, "Start Date", FieldType.DATE, false);
        form.setFields(List.of(field));

        Map<Long, String> values = new HashMap<>();
        values.put(5L, LocalDate.now().minusDays(1).toString());

        assertThrows(InvalidSubmissionException.class, () -> validator.validate(form, values));
    }

    @Test
    void rejectsInvalidColor() {
        FormField field = field(6L, "Color", FieldType.COLOR, false);
        form.setFields(List.of(field));

        Map<Long, String> values = new HashMap<>();
        values.put(6L, "#GGGGGG");

        assertThrows(InvalidSubmissionException.class, () -> validator.validate(form, values));
    }

    @Test
    void rejectsInvalidSelectOption() {
        FormField field = field(7L, "Role", FieldType.SELECT, false);
        FormFieldOption option = option(10L, "Dev", "dev", 1, field);
        field.setOptions(java.util.Set.of(option));
        form.setFields(List.of(field));

        Map<Long, String> values = new HashMap<>();
        values.put(7L, "qa");

        assertThrows(InvalidSubmissionException.class, () -> validator.validate(form, values));
    }

    @Test
    void acceptsValidSubmission() {
        FormField text = field(8L, "Name", FieldType.TEXT, true);
        FormField number = field(9L, "Score", FieldType.NUMBER, false);
        FormField date = field(10L, "Start Date", FieldType.DATE, false);
        FormField color = field(11L, "Color", FieldType.COLOR, false);
        FormField select = field(12L, "Role", FieldType.SELECT, true);
        FormFieldOption option = option(20L, "Dev", "dev", 1, select);
        select.setOptions(java.util.Set.of(option));
        form.setFields(List.of(text, number, date, color, select));

        Map<Long, String> values = new HashMap<>();
        values.put(8L, "Alice");
        values.put(9L, "42");
        values.put(10L, LocalDate.now().plusDays(1).toString());
        values.put(11L, "#AABBCC");
        values.put(12L, "dev");

        assertDoesNotThrow(() -> validator.validate(form, values));
    }

    private FormField field(Long id, String label, FieldType type, boolean required) {
        FormField field = new FormField();
        field.setId(id);
        field.setLabel(label);
        field.setType(type);
        field.setRequired(required);
        field.setDisplayOrder(0);
        return field;
    }

    private FormFieldOption option(Long id, String label, String value, int order, FormField field) {
        FormFieldOption option = new FormFieldOption();
        option.setId(id);
        option.setLabel(label);
        option.setValue(value);
        option.setDisplayOrder(order);
        option.setField(field);
        return option;
    }
}
