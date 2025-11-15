
package com.devtective.devtective.validation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devtective.devtective.dominio.user.UserRequestDTO;

@Service
public class UserValidationService {
    private final List<CreateUserValidationRule> rules;

    public UserValidationService(List<CreateUserValidationRule> rules) {
        this.rules = rules;
    }

    public void validateCreateUser(UserRequestDTO data) {
        for (CreateUserValidationRule rule : rules) {
            rule.validate(data);
        }
    }

}