package com.devtective.devtective.dominio.worker;

public record WorkerRequestDTO(
        Long id,
        String firstName,
        String lastName,
        Long positionId,
        Long userId
) {

}
