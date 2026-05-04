package com.topcv.forms.repository;

import com.topcv.forms.model.SubmissionValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionValueRepository extends JpaRepository<SubmissionValue, Long> {
    boolean existsByFieldId(Long fieldId);
}
