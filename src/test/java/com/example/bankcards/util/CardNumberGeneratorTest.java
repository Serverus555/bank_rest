package com.example.bankcards.util;

import com.example.bankcards.util.business.CardNumberGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CardNumberGeneratorTest {

    @Test
    void generateMaskedNumber() {
        assertThat(getCardNumberGenerator().generateCardNumber()).matches("^\\d{16}$");
    }

    private CardNumberGenerator getCardNumberGenerator() {
        return new CardNumberGenerator();
    }

}
