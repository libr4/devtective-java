package com.devtective.devtective.service.user.validation.create;

import org.springframework.stereotype.Component;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.exception.ConflictException;
import com.devtective.devtective.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailAlreadyExistsRule implements CreateUserValidationRule {

    private final UserRepository userRepository;

    @Override
    public void validate(UserRequestDTO data) {
        if (data.email() == null || data.email().isBlank()) return;
        AppUser user = userRepository.findByEmail(data.email());
        if (user != null) throw new ConflictException("Email already exists: " + data.email());
    }
}
