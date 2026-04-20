package com.example.bankcards.service.admin;

import com.example.bankcards.entity.BlockCardRequest;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.repository.BlockCardRequestRepository;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminBlockCardService {

    private final CardRepository cardRepository;
    private final BlockCardRequestRepository blockCardRequestRepository;

    public Page<BlockCardRequest> getPendingBlockRequests(Pageable pageable) {
        return blockCardRequestRepository.findAllByCompletedFalse(pageable);
    }

    @Transactional
    public void block(Long cardId) {
        int updated = cardRepository.updateStatus(cardId, CardStatus.BLOCKED);
        if (updated != 1) {
            throw new CardNotFoundException();
        }
        blockCardRequestRepository.markCompletedByCardId(cardId);
    }

}
