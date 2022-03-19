package com.example.socialnetworkfx.domain.validators;

public interface Validator<T>{
    void validate(T entity) throws ValidationException;
}