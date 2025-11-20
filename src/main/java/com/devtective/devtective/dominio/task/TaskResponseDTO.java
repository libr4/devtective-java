package com.devtective.devtective.dominio.task;

import java.time.LocalDateTime;

public record TaskResponseDTO(
        String title,
        String description,
        String status,
        String priority,
        String type,
        String projectName,
        String technology,
        String assignedToFullName,
        String createdByIdFullName,
        LocalDateTime deadline,
        Long taskNumber
) {}