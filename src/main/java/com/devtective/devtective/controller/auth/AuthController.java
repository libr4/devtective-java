package com.devtective.devtective.controller.auth;

import com.devtective.devtective.dominio.auth.LoginResponseDTO;
import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.dominio.user.UserResponseDTO;
import com.devtective.devtective.repository.UserRepository;
import com.devtective.devtective.security.TokenService;
import com.devtective.devtective.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    ResponseEntity<String> testRoute() {
        return ResponseEntity.ok("Test completed");
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponseDTO> login(@RequestBody @Validated UserRequestDTO user) {

        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(user.username(), user.password());

        AppUser loginUser = userRepository.findByEmail(user.email());

        if (loginUser == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        String token = tokenService.generateToken((AppUser) auth.getPrincipal());
        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(true)  // Set to false for local development
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        AppUser loggedUser = userService.findByUsername(user.username());
        System.out.println(3);
        LoginResponseDTO response = new LoginResponseDTO("Login successful", loggedUser.getUsername(), loggedUser.getUserId());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
        //return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Validated UserRequestDTO data) {
        UserResponseDTO response = userService.createUser(data);
        return ResponseEntity.ok(response);
    }
}
