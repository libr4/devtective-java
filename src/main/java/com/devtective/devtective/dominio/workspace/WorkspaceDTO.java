package com.devtective.devtective.dominio.workspace;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;


public record WorkspaceDTO(UUID id, @NotBlank String name) {
    public static WorkspaceDTO from(Workspace w) {
        return new WorkspaceDTO(w.getPublicId(), w.getName());
    }
}