package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.validator.routines.EmailValidator;

public class EmailConstraintValidator implements ConstraintValidator<ValidEmail, String> {
    private final EmailValidator validator = EmailValidator.getInstance(true, true);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return validator.isValid(value);
    }
}