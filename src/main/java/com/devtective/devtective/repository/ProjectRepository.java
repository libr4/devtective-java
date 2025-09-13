package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.worker.Worker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByPublicId(UUID publicId);
    List<Project> findByCreatedBy(Worker worker);
    List<Project> findAllByCreatedById(Long workerId);

}
