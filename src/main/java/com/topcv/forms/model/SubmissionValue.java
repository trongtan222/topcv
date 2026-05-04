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
@Table(name = "submission_values")
@Getter
@Setter
public class SubmissionValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "submission_id")
    private FormSubmission submission;

    @ManyToOne(optional = false)
    @JoinColumn(name = "field_id")
    private FormField field;

    @Column(nullable = false, length = 2000)
    private String value;
}
