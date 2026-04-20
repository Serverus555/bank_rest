package com.example.bankcards.service.user;

import com.example.bankcards.dto.user.in.TransferRequest;
import com.example.bankcards.entity.BlockCardRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.QCard;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.exception.card.InsufficientBalanceException;
import com.example.bankcards.exception.card.WrongOperationException;
import com.example.bankcards.repository.BlockCardRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserCardService {

    private final CardRepository cardRepository;
    private final BlockCardRequestRepository blockCardRequestRepository;

    public Page<Card> getUserCards(Pageable pageable, @Nullable Predicate predicate, Long userId) {
        BooleanBuilder predicateBuilder = new BooleanBuilder(predicate);
        predicateBuilder.and(QCard.card.ownerId.eq(userId));

        return cardRepository.findAll(predicateBuilder, pageable);
    }

    @Transactional
    public void requestBlock(Long cardId, Long requesterId) {
        Card card = cardRepository.findByIdAndOwnerId(cardId, requesterId)
            .orElseThrow(CardNotFoundException::new);

        ensureOperationsAllowed(card, requesterId);

        BlockCardRequest blockCardRequest = BlockCardRequest.builder()
            .completed(false)
            .card(card)
            .build();

        blockCardRequestRepository.save(blockCardRequest);
    }

    @Transactional
    public void transfer(TransferRequest transferRequest, Long requesterUserId) {
        // Один запрос в бд
        List<Card> cards = cardRepository.findAllByIdInAndOwnerIdOrderByIdAsc(
            List.of(transferRequest.source(), transferRequest.target()),
            requesterUserId);

        if (cards.size() != 2) {
            throw new CardNotFoundException();
        }

        ensureOperationsAllowed(cards.get(0), requesterUserId);
        ensureOperationsAllowed(cards.get(1), requesterUserId);

        final Card source;
        final Card target;
        {
            Card first = cards.get(0);
            Card second = cards.get(1);
            source = first.getId().equals(transferRequest.source()) ? first : second;
            target = first.getId().equals(transferRequest.target()) ? first : second;
        }

        final BigDecimal amount = transferRequest.amount();

        if (amount.compareTo(BigDecimal.ZERO) < 1) {
            throw new WrongOperationException("Сумма перевода должна быть больше 0");
        }
        if (source.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        source.setBalance(source.getBalance().subtract(amount));
        target.setBalance(target.getBalance().add(amount));

        cardRepository.save(source);
        cardRepository.save(target);
    }

    public BigDecimal getBalance(Long cardId, Long requesterUserId) {
        Card card = cardRepository.findByIdAndOwnerId(cardId, requesterUserId)
            .orElseThrow(CardNotFoundException::new);

        return card.getBalance();
    }

    public void ensureOperationsAllowed(Card card, Long requesterUserId) throws WrongOperationException {
        if (card.calculateStatus().equals(CardStatus.BLOCKED)) {
            throw new WrongOperationException("Карта заблокирована");
        }
        if (card.calculateStatus().equals(CardStatus.EXPIRED)) {
            throw new WrongOperationException("Истёк срок действия");
        }
        // Запасная проверка. Уже учтено в методах поиска в БД
        if (!card.getOwnerId().equals(requesterUserId)) {
            throw new CardNotFoundException();
        }
    }
}
