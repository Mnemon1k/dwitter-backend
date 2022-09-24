package com.mnemon1k.dwitter.User;

import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    @Autowired
    UserRepository repository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Optional<User> userFromDb = repository.findByUsername(value);
        return userFromDb.isEmpty();
    }
}
