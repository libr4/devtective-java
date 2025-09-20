package com.devtective.devtective.dominio.project;

public record LeaderDTO(
        String publicId,
        String displayName,
        String username,
        String avatarUrl
) {}
