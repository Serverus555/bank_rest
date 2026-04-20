package com.example.bankcards.exception.abstracts;

// Планировалась доп логика
public abstract class NotFoundException extends BusinessException {
    public NotFoundException() {

    }

    public NotFoundException(String message) {
        super(message);
    }
}
