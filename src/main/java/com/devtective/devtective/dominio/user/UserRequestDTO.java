package com.devtective.devtective.dominio.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank
        String username,
        @NotBlank @Email @Size(max = 120)
        String email,
        @Size(min = 8, max = 100, message = "Password must be 8–100 chars")
        String password,
        Long roleId) {

}
