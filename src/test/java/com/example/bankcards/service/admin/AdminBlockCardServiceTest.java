package com.example.bankcards.service.admin;

import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.repository.BlockCardRequestRepository;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.bankcards.service.CardTestUtils.CARD_ID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBlockCardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private BlockCardRequestRepository blockCardRequestRepository;

    @InjectMocks
    private AdminBlockCardService adminBlockCardService;

    @Test
    void cardNotFound() {
        when(cardRepository.updateStatus(eq(CARD_ID), eq(CardStatus.BLOCKED)))
            .thenReturn(0);

        assertThatThrownBy(() -> adminBlockCardService.block(CARD_ID))
            .isInstanceOf(CardNotFoundException.class);
    }

    @Test
    void ok() {
        when(cardRepository.updateStatus(eq(CARD_ID), eq(CardStatus.BLOCKED)))
            .thenReturn(1);

        adminBlockCardService.block(CARD_ID);

        verify(blockCardRequestRepository).markCompletedByCardId(eq(CARD_ID));
    }
}
