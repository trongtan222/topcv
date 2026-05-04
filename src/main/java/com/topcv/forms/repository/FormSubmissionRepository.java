package com.topcv.forms.repository;

import com.topcv.forms.model.FormSubmission;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormSubmissionRepository extends JpaRepository<FormSubmission, Long> {
    List<FormSubmission> findByFormIdOrderBySubmittedAtDesc(Long formId);

    Page<FormSubmission> findAllByOrderBySubmittedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"form", "values", "values.field"})
    Page<FormSubmission> findAllWithValuesByOrderBySubmittedAtDesc(Pageable pageable);

    boolean existsByFormId(Long formId);
}
