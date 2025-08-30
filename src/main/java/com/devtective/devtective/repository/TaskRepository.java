package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Task findByProjectAndTaskNumber(Project project, Long taskNumber);

    List<Task> findByProjectId(Long projectId);
    long countByProjectId(Long projectId);
    void deleteByProjectIdAndTaskNumber(Long projectId, Long taskNumber);
}
