package com.devtective.devtective.dominio.user;

public record UserRequestDTO(String username, String email, String password, long roleId) {

}
