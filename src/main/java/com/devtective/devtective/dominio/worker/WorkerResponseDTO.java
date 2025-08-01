package com.devtective.devtective.dominio.worker;

public record WorkerResponseDTO(
        Long id,
        String firstName,
        String lastName,
        Long positionId,
        Long userId
) {
}
