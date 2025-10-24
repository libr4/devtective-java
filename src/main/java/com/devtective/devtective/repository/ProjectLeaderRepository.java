package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.project.ProjectLeader;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProjectLeaderRepository extends JpaRepository<ProjectLeader, Long> {
    boolean existsByProjectPublicIdAndWorkerId(UUID projectPublicId, Long workerId);
    boolean existsByProjectIdAndWorkerId(Long projectId, Long workerId);
}