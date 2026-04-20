package com.example.bankcards.exception.card;

import com.example.bankcards.exception.abstracts.BusinessException;

public class InsufficientBalanceException extends BusinessException {

    public InsufficientBalanceException() {
        super("Недостаточно средств");
    }
}
