package com.devtective.devtective.validation;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.exception.ConflictException;
import com.devtective.devtective.repository.UserRepository;

public class EmailAlreadyExistsRule implements CreateUserValidationRule {

    private final UserRepository userRepository;

    public EmailAlreadyExistsRule(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validate(UserRequestDTO data) {
        AppUser user = userRepository.findByEmail(data.email());
        if (user != null) throw new ConflictException("Email already exists: " + data.email());
    }
}
