package com.sudharsan.smartgrading.repository;

import com.sudharsan.smartgrading.model.Mark;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MarkRepository extends JpaRepository<Mark, Long> {
    List<Mark> findByStudentId(Long studentId);
    List<Mark> findBySubjectId(Long subjectId);
}
