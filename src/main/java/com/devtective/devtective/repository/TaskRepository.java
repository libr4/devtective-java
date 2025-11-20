package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Task findByProjectAndTaskNumber(Project project, Long taskNumber);

    @Query("""
        SELECT t FROM Task t
        LEFT JOIN t.assignedTo w
        LEFT JOIN w.userId u
        WHERE t.project.publicId = :projectPublicId
        AND (:usernames IS NULL OR u.username IN :usernames)
        AND (:types IS NULL OR t.taskType.name IN :types)
        AND (:priorities IS NULL OR t.taskPriority.name IN :priorities)
        AND (:statuses IS NULL OR t.taskStatus.name IN :statuses)
        AND (:q IS NULL OR t.title ILIKE :q)
    """)
    List<Task> findByProjectPublicIdAndParams(
        @Param("projectPublicId") UUID projectPublicId, 
        @Param("q") String q,
        @Param("usernames") List<String> usernames, 
        @Param("types") List<String> types, 
        @Param("priorities") List<String> priorities, 
        @Param("statuses") List<String> statuses);
    List<Task> findByProjectPublicId(UUID projectPublicId);
    List<Task> findByProjectId(Long projectId);
    long countByProjectId(Long projectId);
    void deleteByProjectIdAndTaskNumber(Long projectId, Long taskNumber);
}
