package com.example.bankcards.util.business;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class CardNumberGenerator {

    public String generateCardNumber() {
        // Можно было бы сделать по-умному - контрольная сумма, проверка по sha-256 хешу в БД, но предпочитаю соблюдать KISS и YAGNI
        Random random = new Random();
        return String.format("%08d%08d", random.nextInt(100_000_000), random.nextInt(100_000_000));
    }
}
