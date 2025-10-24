package com.devtective.devtective.dominio.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TaskRequestDTO(
        String title,
        String description,
        Long taskStatusId,
        Long taskPriorityId,
        Long taskTypeId,
        UUID projectPublicId,
        String technology,
        UUID assignedToId,
        UUID createdById,
        LocalDateTime deadline,

        Long taskNumber
) {
    public TaskRequestDTO withProjectId(UUID projectPublicId) {
        return new TaskRequestDTO(
                title,
                description,
                taskStatusId,
                taskPriorityId,
                taskTypeId,
                projectPublicId,
                technology,
                assignedToId,
                createdById,
                deadline,
                taskNumber
        );
    }

    public TaskRequestDTO withCreatedById(UUID userPublicId) {
        return new TaskRequestDTO(
                title,
                description,
                taskStatusId,
                taskPriorityId,
                taskTypeId,
                projectPublicId,
                technology,
                assignedToId,
                userPublicId,
                deadline,
                taskNumber
        );
    }

    // Helper: copy with a new projectId and taskNumber
    public TaskRequestDTO withProjectPublicIdAndTaskNumber(UUID projectPublicId, Long taskNumber) {
        return new TaskRequestDTO(
                title,
                description,
                taskStatusId,
                taskPriorityId,
                taskTypeId,
                projectPublicId,
                technology,
                assignedToId,
                createdById,
                deadline,
                taskNumber
        );
    }
}