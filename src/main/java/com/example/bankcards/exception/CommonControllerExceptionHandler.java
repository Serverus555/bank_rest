package com.example.bankcards.exception;

import com.example.bankcards.dto.errorresponses.DtoError;
import com.example.bankcards.dto.errorresponses.ParamError;
import com.example.bankcards.exception.abstracts.BusinessException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice(basePackages = "com.example.bankcards.controller") // Не перехватываем то что не относится к api (404)
public class CommonControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DtoError> handleDtoError(MethodArgumentNotValidException ex) {
        DtoError dtoError = new DtoError(new HashMap<>(), new ArrayList<>());

        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            if (error instanceof FieldError fieldError) {
                dtoError.fields()
                    .computeIfAbsent(fieldError.getField(), key -> new ArrayList<>())
                    .add(fieldError.getDefaultMessage());
            }
            else {
                dtoError.otherErrors().add(error.getDefaultMessage());
            }
        }

        return ResponseEntity.badRequest().body(dtoError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ParamError> handleParamErrors(ConstraintViolationException ex) {
        ParamError paramError = new ParamError(new HashMap<>());
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            paramError.errors().put(
                violation.getPropertyPath().toString(),
                Objects.requireNonNullElse(violation.getMessage(), "")
            );
        }
        return ResponseEntity.badRequest().body(paramError);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ParamError> handleParamErrors(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(new ParamError(Map.of(ex.getParameterName(), ex.getMessage())));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessException(BusinessException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> badCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверные учётные данные");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleCommonException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Непредвиденная ошибка");
    }
}
