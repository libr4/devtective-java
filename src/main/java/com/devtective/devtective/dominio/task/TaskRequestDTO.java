package com.devtective.devtective.dominio.task;

import java.time.LocalDate;

public record TaskRequestDTO(
        String title,
        String description,
        Long taskStatusId,
        Long taskPriorityId,
        Long taskTypeId,
        Long projectId,
        String technology,
        Long assignedToId,
        Long createdById,
        LocalDate deadline,

        Long taskNumber
) {}