package com.example.bankcards.util.controller.validate;

import com.example.bankcards.dto.user.in.TransferRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CardsDifferentConstraint.Validator.class)
public @interface CardsDifferentConstraint {
    String message() default "Карты должны быть разными";

    Class<?>[] groups() default {};
    Class<? extends jakarta.validation.Payload>[] payload() default {};

    class Validator implements ConstraintValidator<CardsDifferentConstraint, TransferRequest> {
        @Override
        public boolean isValid(TransferRequest dto, ConstraintValidatorContext context) {
            return !java.util.Objects.equals(dto.source(), dto.target());
        }
    }
}
