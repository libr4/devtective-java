package com.devtective.devtective.repository;

import com.devtective.devtective.common.dto.IdNameDTO;
import com.devtective.devtective.dominio.task.TaskStatus;
import com.devtective.devtective.dominio.task.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskTypeRepository extends JpaRepository<TaskType, Long> {
    List<IdNameDTO> findAllByOrderByNameAsc();

}
