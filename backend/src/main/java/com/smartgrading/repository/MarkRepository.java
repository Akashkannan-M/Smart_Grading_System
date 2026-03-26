package com.smartgrading.repository;

import com.smartgrading.entity.Mark;
import com.smartgrading.entity.Subject;
import com.smartgrading.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MarkRepository extends JpaRepository<Mark, Long> {
    List<Mark> findByStudent(User student);
    List<Mark> findBySubject(Subject subject);
    List<Mark> findByExamType(Mark.ExamType examType);
    Mark findByStudentAndSubjectAndExamType(User student, Subject subject, Mark.ExamType examType);
}
