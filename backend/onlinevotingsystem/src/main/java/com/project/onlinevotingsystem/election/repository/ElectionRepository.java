package com.project.onlinevotingsystem.election.repository;

import com.project.onlinevotingsystem.election.model.Election;
import com.project.onlinevotingsystem.election.model.ElectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {
    List<Election> findByStatus(ElectionStatus status);
}
