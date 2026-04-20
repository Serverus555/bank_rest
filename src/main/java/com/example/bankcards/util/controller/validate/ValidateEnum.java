package com.example.bankcards.util.controller.validate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidator.class) // Связываем с логикой
public @interface ValidateEnum {
    Class<? extends Enum<?>> target();

    String message() default "Недопустимое значение";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
