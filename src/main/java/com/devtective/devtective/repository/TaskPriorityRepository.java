package com.devtective.devtective.repository;

import com.devtective.devtective.common.dto.IdNameDTO;
import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.task.Task;
import com.devtective.devtective.dominio.task.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskPriorityRepository extends JpaRepository<TaskPriority, Long> {
    List<IdNameDTO> findAllByOrderByNameAsc();

}
