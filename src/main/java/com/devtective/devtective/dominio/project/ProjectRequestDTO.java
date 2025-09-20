package com.devtective.devtective.dominio.project;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProjectRequestDTO(
        Long id,
        String name,
        String description,
        String url,
        LocalDate startDate,
        LocalDate endDate,
        Long createdById,
        UUID workspacePublicId,
        List<UUID> leaderPublicIds,
        List<UUID> memberPublicIds
) {}
