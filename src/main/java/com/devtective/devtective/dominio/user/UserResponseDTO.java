package com.devtective.devtective.dominio.user;

public record UserResponseDTO(String username, String email, long roleId) {
     public static UserResponseDTO convertToDTO(AppUser user) {
        Long roleId = (user.getRole() != null ? user.getRole().getId() : null);
        return new UserResponseDTO(user.getUsername(), user.getEmail(), roleId);
    }
}
