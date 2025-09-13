package com.devtective.devtective.dominio.user;

import com.devtective.devtective.validation.OnLogin;
import com.devtective.devtective.validation.OnRegister;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.Default;

public record UserRequestDTO(
        @NotBlank(groups = {Default.class, OnLogin.class, OnRegister.class})
        String username,
        @NotBlank(groups = {Default.class, OnRegister.class}) @Email(groups = {Default.class, OnLogin.class, OnRegister.class}) @Size(groups = {Default.class, OnLogin.class, OnRegister.class}, max = 120)
        String email,
        @Size(groups = {Default.class, OnLogin.class, OnRegister.class}, min = 1, max = 100, message = "Password must be 8–100 chars")
        String password,
        Long roleId,
        @NotBlank(groups={Default.class, OnRegister.class})
        String fullName) {

}
