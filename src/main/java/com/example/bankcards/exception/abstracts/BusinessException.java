package com.example.bankcards.exception.abstracts;

public abstract class BusinessException extends RuntimeException {

    public BusinessException() {

    }

    public BusinessException(String message) {
        super(message);
    }
}
