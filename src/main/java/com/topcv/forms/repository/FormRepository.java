package com.topcv.forms.repository;

import com.topcv.forms.model.Form;
import com.topcv.forms.model.FormStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormRepository extends JpaRepository<Form, Long> {
    List<Form> findAllByOrderByDisplayOrderAscIdAsc();

    Page<Form> findAllByOrderByDisplayOrderAscIdAsc(Pageable pageable);

    List<Form> findByStatusOrderByDisplayOrderAscIdAsc(FormStatus status);

    @EntityGraph(attributePaths = {"fields", "fields.options"})
    Optional<Form> findWithFieldsById(Long id);
}
