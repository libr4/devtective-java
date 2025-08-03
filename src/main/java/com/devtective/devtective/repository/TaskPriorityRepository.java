package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.project.Project;
import com.devtective.devtective.dominio.task.Task;
import com.devtective.devtective.dominio.task.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskPriorityRepository extends JpaRepository<TaskPriority, Long> {

}
