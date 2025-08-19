package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.project.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    boolean existsByProject_IdAndWorker_Id(Long projectId, Long workerId);
    List<ProjectMember> findAllByWorker_Id(Long workerId);
}