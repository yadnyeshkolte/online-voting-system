package com.project.onlinevotingsystem.admin.repository;

import com.project.onlinevotingsystem.admin.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUser_UserId(Long userId);
}
