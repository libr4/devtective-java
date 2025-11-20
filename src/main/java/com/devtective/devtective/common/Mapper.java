package com.devtective.devtective.common;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.user.UserResponseDTO;

public class Mapper {
    public static UserResponseDTO toUserResponseDTO(AppUser user) {
        Long roleId = (user.getRole() != null ? user.getRole().getId() : null);
        return new UserResponseDTO(user.getUsername(), user.getEmail(), roleId);
    }
}
