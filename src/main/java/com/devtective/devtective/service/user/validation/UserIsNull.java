package com.devtective.devtective.service.user.validation;

import com.devtective.devtective.dominio.user.AppUser;
import com.devtective.devtective.exception.NotFoundException;

public class UserIsNull implements CommonUserValidationRule {

    @Override
    public void validate(AppUser user) {
        if (user == null) {
            throw new NotFoundException("User not found");
        }
    }
    
}
