package com.devtective.devtective.controller.auth;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;
    @GetMapping("/login")
    ResponseEntity<String> testRoute() {
        return ResponseEntity.ok("Test completed");
    }

    @PostMapping("/login")
    ResponseEntity<String> login(@RequestBody UserRequestDTO user) {

        UsernamePasswordAuthenticationToken usernamePassword = new UsernamePasswordAuthenticationToken(user.username(), user.password());

        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        String token = tokenService.generateToken((AppUser) auth.getPrincipal());
        return ResponseEntity.ok().build();
    }
}
