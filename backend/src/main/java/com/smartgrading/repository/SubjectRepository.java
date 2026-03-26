package com.smartgrading.repository;

import com.smartgrading.entity.Subject;
import com.smartgrading.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByStaff(User staff);
}
