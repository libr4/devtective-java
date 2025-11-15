package com.devtective.devtective.validation;

import com.devtective.devtective.dominio.user.UserRequestDTO;

public interface ValidationRule<T> {
    void validate(T data);
}
