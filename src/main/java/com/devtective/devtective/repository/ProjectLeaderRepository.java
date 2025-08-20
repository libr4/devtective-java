package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.project.ProjectLeader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectLeaderRepository extends JpaRepository<ProjectLeader, Long> {
    boolean existsByProjectIdAndWorkerId(Long projectId, Long workerId);
}