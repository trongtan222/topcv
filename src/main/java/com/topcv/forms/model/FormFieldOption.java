package com.topcv.forms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "form_field_options")
@Getter
@Setter
public class FormFieldOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @ManyToOne(optional = false)
    @JoinColumn(name = "field_id")
    private FormField field;
}
