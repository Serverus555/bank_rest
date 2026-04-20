package com.example.bankcards.exception.card;

import com.example.bankcards.exception.abstracts.BusinessException;

public class WrongOperationException extends BusinessException {

    public WrongOperationException(String message) {
        super(message);
    }
}
