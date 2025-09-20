package com.devtective.devtective.dominio.user;

import java.util.UUID;

public record CurrentUserResponseDTO(String username, String email, long roleId, UUID publicId, String displayName) {
}
