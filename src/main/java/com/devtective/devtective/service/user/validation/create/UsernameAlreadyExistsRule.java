package com.devtective.devtective.service.user.validation.create;

import org.springframework.stereotype.Component;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.exception.ConflictException;
import com.devtective.devtective.exception.NotFoundException;
import com.devtective.devtective.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UsernameAlreadyExistsRule implements CreateUserValidationRule {

    private final UserRepository userRepository;

    @Override
    public void validate(UserRequestDTO data) {
        if (data.username() == null || data.username().isBlank()) return;
        AppUser user = userRepository.findByUsername(data.username())
            .orElse(null);
        if (user != null) throw new ConflictException("Username already exists: " + data.username());
    }
}
