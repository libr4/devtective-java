package com.devtective.devtective.service.common.validation;

public interface ValidationRule<T> {
    void validate(T data);
}
