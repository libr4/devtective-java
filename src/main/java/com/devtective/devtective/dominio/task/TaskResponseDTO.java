package com.devtective.devtective.dominio.task;

import java.time.LocalDate;

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
        LocalDate deadline,
        Long taskNumber
) {}