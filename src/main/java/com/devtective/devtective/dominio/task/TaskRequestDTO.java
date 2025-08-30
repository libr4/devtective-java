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
) {
    public TaskRequestDTO withProjectId(Long projectId) {
        return new TaskRequestDTO(
                title,
                description,
                taskStatusId,
                taskPriorityId,
                taskTypeId,
                projectId,
                technology,
                assignedToId,
                createdById,
                deadline,
                taskNumber
        );
    }

    // Helper: copy with a new projectId and taskNumber
    public TaskRequestDTO withProjectIdAndTaskNumber(Long projectId, Long taskNumber) {
        return new TaskRequestDTO(
                title,
                description,
                taskStatusId,
                taskPriorityId,
                taskTypeId,
                projectId,
                technology,
                assignedToId,
                createdById,
                deadline,
                taskNumber
        );
    }
}