package com.example.bankcards.util.controller.validate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Stream;

public class EnumValidator implements ConstraintValidator<ValidateEnum, String> {
    private List<String> acceptedValues;

    @Override
    public void initialize(ValidateEnum annotation) {
        acceptedValues = Stream.of(annotation.target().getEnumConstants())
            .map(Enum::name)
            .toList();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // Заранее заполненный список быстрее try-catch (расход на перехват исключения т.д.)
        return acceptedValues.contains(value);
    }
}
