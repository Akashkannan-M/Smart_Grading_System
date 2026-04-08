package com.sudharsan.smartgrading.repository;

import com.sudharsan.smartgrading.model.Role;
import com.sudharsan.smartgrading.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByRegNo(String regNo);
    Optional<User> findByEmpId(String empId);
    Optional<User> findFirstByName(String name);
    List<User> findByRole(Role role);
    List<User> findByRoleOrderByRegNoAsc(Role role);
}
