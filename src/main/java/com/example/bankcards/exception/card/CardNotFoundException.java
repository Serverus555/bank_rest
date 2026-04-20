package com.example.bankcards.exception.card;

import com.example.bankcards.exception.abstracts.NotFoundException;

public class CardNotFoundException extends NotFoundException {

    public CardNotFoundException() {
        super("Карта не найдена");
    }
}
