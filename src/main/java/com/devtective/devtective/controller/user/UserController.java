package com.devtective.devtective.controller.user;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.dominio.user.UserResponseDTO;
import com.devtective.devtective.dominio.user.UserWithFullNameDTO;
import com.devtective.devtective.service.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMe(@AuthenticationPrincipal AppUser me) {
        return ResponseEntity.ok(convertToDTO(me));
    }

    //@PreAuthorize("hasRole('ADMIN')")
    //@GetMapping
    //public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        //List<AppUser> users = userService.getAllUsers();
        //List<UserResponseDTO> usersResponse = convertToDTOList(users);
        //return ResponseEntity.ok(usersResponse);
    //}

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> listAll(Pageable pageable) {
        Page<UserResponseDTO> users = userService.getAllUsersPaginated(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("{username}")
    @PreAuthorize("@perm.selfOrAdmin(authentication, #username)")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String username) {
        UserResponseDTO response = userService.fetchOwnUser(username);
        return ResponseEntity.ok(response);
    }

    @PutMapping("{username}")
    @PreAuthorize("@perm.selfOrAdmin(authentication, #username)")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable String username,
                                                      @Valid @RequestBody UserRequestDTO user) {
        UserResponseDTO response = userService.updateUserResponse(username, user);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMe(@AuthenticationPrincipal AppUser principal,
                                                    @Valid @RequestBody UserRequestDTO user) {
        String username = user.username();
        UserResponseDTO response = userService.updateOwnUser(user, username);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{username}")
    @PreAuthorize("@perm.selfOrAdmin(authentication, #username)")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        userService.deleteByUsername(username);
        String response = "User removed successfully";
        return ResponseEntity.ok(response);
    }

    /** Usuários relacionados são os usuários que estão no mesmo projeto ou workspace
     * e podem ser vistos para convite direto para serem membros ou líderes de projetos */
    @GetMapping("related")
    public ResponseEntity<List<UserWithFullNameDTO>> getRelatedUsers(@AuthenticationPrincipal AppUser me) {
        List<UserWithFullNameDTO> response = userService.getRelatedUsers(me);
        return ResponseEntity.ok(response);
    }

    private UserResponseDTO convertToDTO(AppUser user) {
        Long roleId = (user.getRole() != null ? user.getRole().getId() : null);
        return new UserResponseDTO(user.getUsername(), user.getEmail(), roleId);
    }
}
