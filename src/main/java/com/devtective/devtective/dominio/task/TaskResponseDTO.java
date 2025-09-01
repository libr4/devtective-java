package com.devtective.devtective.dominio.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponseDTO(
        String title,
        String description,
        String taskStatus,
        String taskPriorityName,
        String taskTypeName,
        String projectName,
        String technologyName,
        String assignedToFullName,
        String createdByIdFullName,
        LocalDateTime deadline,
        Long taskNumber
) {}