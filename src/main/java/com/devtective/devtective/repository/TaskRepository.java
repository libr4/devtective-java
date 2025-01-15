package com.devtective.devtective.repository;

import com.devtective.devtective.dominio.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
