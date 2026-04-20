package com.example.bankcards.service.admin;

import com.example.bankcards.dto.admin.in.CreateCardRequest;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.business.CardNumberCrypt;
import com.example.bankcards.util.business.CardNumberGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.example.bankcards.service.CardTestUtils.CARD_ID;
import static com.example.bankcards.service.CardTestUtils.USER_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminCardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardNumberGenerator cardNumberGenerator;

    @Mock
    private CardNumberCrypt cardNumberCrypt;

    @InjectMocks
    private AdminCardService adminCardService;

    @Nested
    class CreateTest {

        private static final String LAST4DIGITS = "3456";
        private static final String NUMBER = "1234" + "5678" + "9012" + LAST4DIGITS;
        private static final String ENCRYPTED_NUMBER = "encrypted123";

        @Test
        void cardNumberEncrypted() {
            callCreate();
            verify(cardRepository).save(argThat(card -> card.getEncryptedNumber().equals(ENCRYPTED_NUMBER)));
        }

        @Test
        void hasLast4Digits() {
            callCreate();
            verify(cardRepository).save(argThat(card -> card.getLast4Digits().equals(LAST4DIGITS)));
        }

        private void callCreate() {
            when(cardNumberGenerator.generateCardNumber()).thenReturn(NUMBER);
            when(cardNumberCrypt.encryptNumber(eq(NUMBER))).thenReturn(ENCRYPTED_NUMBER);
            adminCardService.create(new CreateCardRequest(USER_ID, "text", LocalDate.now(), BigDecimal.valueOf(100)));
        }
    }

    @Nested
    class ActivateTest {

        @Test
        void ok() {
            when(cardRepository.updateStatus(eq(CARD_ID), eq(CardStatus.ACTIVE)))
                .thenReturn(1);

            adminCardService.activate(CARD_ID);

            verify(cardRepository).updateStatus(CARD_ID, CardStatus.ACTIVE);
        }

        @Test
        void cardNotFound() {
            when(cardRepository.updateStatus(eq(CARD_ID), eq(CardStatus.ACTIVE)))
                .thenReturn(0);

            assertThatThrownBy(() -> adminCardService.activate(CARD_ID))
                .isInstanceOf(CardNotFoundException.class);
        }
    }
}
