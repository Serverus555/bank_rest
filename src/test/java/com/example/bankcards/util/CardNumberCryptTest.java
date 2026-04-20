package com.example.bankcards.util;

import com.example.bankcards.util.business.CardNumberCrypt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardNumberCryptTest {

    private static final String TEST_SECRET_KEY = "12345678901234567890123456789012";
    private static final String NUMBER = "1234" + "5678" + "9012" + "3456";

    @Test
    void generateMaskedNumber() {
        assert getCardNumberCrypt().generateMaskedNumber("1234").equals("**** **** **** 1234");
    }

    @Test
    void encryptDecrypt() {
        CardNumberCrypt cardNumberCrypt = getCardNumberCrypt();
        String encrypted = cardNumberCrypt.encryptNumber(NUMBER);
        String decrypted = cardNumberCrypt.decryptNumber(encrypted);

        assertEquals(NUMBER, decrypted);
    }

    private CardNumberCrypt getCardNumberCrypt() {
        return new CardNumberCrypt(TEST_SECRET_KEY);
    }

}
