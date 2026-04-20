package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CardTestUtils {
    public static final Long USER_ID = 1L;
    public static final Long CARD_ID = 1L;

    public static Card okCard() {
        return Card.builder()
            .id(CARD_ID)
            .encryptedNumber("encrypted")
            .last4Digits("1234")
            .ownerId(USER_ID)
            .status(CardStatus.ACTIVE)
            .expiration(LocalDate.now().plusDays(1))
            .balance(new BigDecimal("100.00"))
            .build();
    }
}
