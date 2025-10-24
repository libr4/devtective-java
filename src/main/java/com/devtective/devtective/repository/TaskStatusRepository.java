package com.devtective.devtective.repository;

import com.devtective.devtective.common.dto.IdNameDTO;
import com.devtective.devtective.dominio.task.TaskPriority;
import com.devtective.devtective.dominio.task.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {
    List<IdNameDTO> findAllByOrderByNameAsc();

}
