package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.task.TaskPriority;
import com.devtective.devtective.dominio.task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

}
