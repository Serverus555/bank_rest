package com.example.bankcards.service.admin;

import com.example.bankcards.dto.admin.in.CreateCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.business.CardNumberCrypt;
import com.example.bankcards.util.business.CardNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCardService {

    private final CardRepository cardRepository;
    private final CardNumberGenerator cardNumberGenerator;
    private final CardNumberCrypt cardNumberCrypt;

    @Transactional
    public Card create(CreateCardRequest createCardRequest) {
        String number = cardNumberGenerator.generateCardNumber();
        Card card = Card.builder()
            .encryptedNumber(cardNumberCrypt.encryptNumber(number))
            .last4Digits(number.substring(12))
            .ownerId(createCardRequest.ownerId())
            .ownerText(createCardRequest.ownerText())
            .expiration(createCardRequest.expiration())
            .status(CardStatus.ACTIVE)
            .balance(createCardRequest.balance())
            .build();

        return cardRepository.save(card);
    }

    public Page<Card> getAll(Pageable pageable) {
        return cardRepository.findAll(pageable);
    }

    @Transactional
    public void activate(Long id) {
        int updated = cardRepository.updateStatus(id, CardStatus.ACTIVE);
        if (updated != 1) {
            throw new CardNotFoundException();
        }

    }

    @Transactional
    public void delete(Long id) {
        cardRepository.deleteById(id);
    }

}
