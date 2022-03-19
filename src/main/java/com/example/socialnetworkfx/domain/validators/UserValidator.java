package com.example.socialnetworkfx.domain.validators;

import com.example.socialnetworkfx.domain.User;

import java.util.Objects;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        if(Objects.equals(entity.getLast_name(), ""))
            throw new ValidationException("Numele de familie nu poate fi nul");
        if(Objects.equals(entity.getFirst_name(), ""))
            throw new ValidationException("Prenumele nu poate fi nul");

    }
}
