package com.devtective.devtective.controller.user;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.dominio.user.UserResponseDTO;
import com.devtective.devtective.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<AppUser> users = userService.getAllUsers();
        System.out.println(users);
        List<UserResponseDTO> usersResponse = convertToDTOList(users);
        return ResponseEntity.ok(usersResponse);
    }

    private List<UserResponseDTO> convertToDTOList(List<AppUser> users) {
        return users.stream()
                .map(user -> new UserResponseDTO(user.getUsername(), user.getEmail(), user.getRole().getId()))
                .collect(Collectors.toList());
    }
}
