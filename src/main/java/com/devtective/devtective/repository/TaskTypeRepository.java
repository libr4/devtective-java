package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.task.TaskStatus;
import com.devtective.devtective.dominio.task.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskTypeRepository extends JpaRepository<TaskType, Long> {

}
