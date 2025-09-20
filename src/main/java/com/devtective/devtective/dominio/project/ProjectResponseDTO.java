package com.devtective.devtective.dominio.project;

import com.devtective.devtective.dominio.workspace.WorkspaceDTO;

import java.time.LocalDate;
import java.util.UUID;

public record ProjectResponseDTO(
        UUID publicId,
        String name,
        String description,
        String url,
        LocalDate startDate,
        LocalDate endDate,
        String createdByName,
        WorkspaceDTO workspace
) {}
