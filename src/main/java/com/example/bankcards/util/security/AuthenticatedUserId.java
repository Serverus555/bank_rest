package com.example.bankcards.util.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Если использовать AuthenticationPrincipal с User, то после рестарта security context сбросится.
 *  Токен останется валидным, но приложение забудет про User.
 *  Поэтому храним id в jwt и не завязываемся на контекст.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "T(java.lang.Long).valueOf(subject)", errorOnInvalidType = true)
public @interface AuthenticatedUserId {
}
