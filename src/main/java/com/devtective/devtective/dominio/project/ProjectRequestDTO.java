package com.devtective.devtective.dominio.project;

import java.time.LocalDate;

public record ProjectRequestDTO(
        Long id,
        String name,
        String description,
        String url,
        LocalDate startDate,
        LocalDate endDate,
        Long createdById
) {}
