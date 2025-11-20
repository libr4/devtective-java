
package com.devtective.devtective.service.user.validation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.dominio.user.UserRequestDTO;
import com.devtective.devtective.service.user.validation.create.CreateUserValidationRule;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserValidationService {
    private final List<CreateUserValidationRule> createUserRules;
    private final List<CommonUserValidationRule> commonUserRules;

    public void validateCreateUser(UserRequestDTO data) {
        for (CreateUserValidationRule rule : createUserRules) {
            rule.validate(data);
        }
    }
    public void validateCommonUser(AppUser data) {
        for (CommonUserValidationRule rule : commonUserRules) {
            rule.validate(data);
        }
    }
}