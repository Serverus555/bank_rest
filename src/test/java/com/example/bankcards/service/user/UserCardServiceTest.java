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
import com.example.bankcards.service.CardTestUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.bankcards.service.CardTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private BlockCardRequestRepository blockCardRequestRepository;

    @Spy
    @InjectMocks
    private UserCardService userCardService;

    @BeforeEach
    void setUp() {
    }

    interface UsingEnsureOperationsAllowedTestExtension {

        UserCardService getUserCardService();

        default void ensureOperationsAllowedCalled() {
            Card card = okCard();

            verify(getUserCardService()).ensureOperationsAllowed(any(), any());
        }
    }

    @Nested
    class GetUserCardsTest {

        @Test
        void filterTest() {
            Pageable pageable = PageRequest.of(0, 10);
            Predicate inputPredicate = QCard.card.status.eq(CardStatus.ACTIVE).and(QCard.card.ownerId.eq(USER_ID));

            Predicate expected = new BooleanBuilder(inputPredicate)
                .and(QCard.card.ownerId.eq(USER_ID));

            when(cardRepository.findAll(any(Predicate.class), eq(pageable)))
                .thenReturn(Page.empty());

            userCardService.getUserCards(pageable, inputPredicate, USER_ID);

            ArgumentCaptor<Predicate> predicateCaptor = ArgumentCaptor.forClass(Predicate.class);

            verify(cardRepository).findAll(predicateCaptor.capture(), eq(pageable));

            assertThat(predicateCaptor.getValue()).isEqualTo(expected);
        }

        @Test
        void noInitFilterTest() {
            Pageable pageable = PageRequest.of(0, 10);

            Predicate expected = new BooleanBuilder(null)
                .and(QCard.card.ownerId.eq(USER_ID));

            when(cardRepository.findAll(any(Predicate.class), eq(pageable)))
                .thenReturn(Page.empty());

            userCardService.getUserCards(pageable, null, USER_ID);

            ArgumentCaptor<Predicate> predicateCaptor = ArgumentCaptor.forClass(Predicate.class);

            verify(cardRepository).findAll(predicateCaptor.capture(), eq(pageable));

            assertThat(predicateCaptor.getValue()).isEqualTo(expected);
        }
    }

    @Nested
    class BlockRequestTest implements UsingEnsureOperationsAllowedTestExtension {

        @Test
        void okForActive() {
            Card card = savedCard();

            BlockCardRequest expected = BlockCardRequest.builder()
                .completed(false)
                .card(card)
                .build();

            userCardService.requestBlock(card.getId(), card.getOwnerId());

            ArgumentCaptor<BlockCardRequest> captor = ArgumentCaptor.forClass(BlockCardRequest.class);
            verify(blockCardRequestRepository).save(captor.capture());

            assertThat(captor.getValue())
                .usingRecursiveComparison()
                .isEqualTo(expected);
        }

        @Test
        void usingEnsureOperationAllowed() {

        }

        @Test
        void cardNotFound() {
            when(cardRepository.findByIdAndOwnerId(eq(CARD_ID), eq(USER_ID + 1)))
                .thenReturn(Optional.empty());

            assertThatThrownBy(() -> userCardService.requestBlock(CARD_ID, USER_ID + 1))
                .isInstanceOf(CardNotFoundException.class);
        }

        private Card savedCard() {
            Card card = okCard();

            when(cardRepository.findByIdAndOwnerId(eq(CARD_ID), eq(USER_ID)))
                .thenReturn(Optional.of(card));
            return card;
        }

        @Override
        public UserCardService getUserCardService() {
            return userCardService;
        }
    }

    @Nested
    class TransferTest implements UsingEnsureOperationsAllowedTestExtension {

        @Test
        void errorNoCard() {
            Card source = okCard();
            Card target = targetOkCard();

            when(cardRepository.findAllByIdInAndOwnerIdOrderByIdAsc(any(), any()))
                .thenReturn(List.of(source));

            assertThatThrownBy(() -> transfer(source, target, 20, USER_ID))
                .isInstanceOf(CardNotFoundException.class);
        }

        @Test
        void errorCardNotOwnedByRequester() {
            Card source = okCard();
            Card target = targetOkCard();

            when(cardRepository.findAllByIdInAndOwnerIdOrderByIdAsc(any(), any()))
                .thenReturn(List.of(source));

            assertThatThrownBy(() -> transfer(source, target, 20, USER_ID + 1))
                .isInstanceOf(CardNotFoundException.class);
        }

        @ParameterizedTest
        @ValueSource(ints =  {-10, 0})
        void errorOnAmountNotGreaterZero(Integer amount) {
            Card source = okCard();
            Card target = targetOkCard();
            prepareRepo(source, target);

            assertThatThrownBy(() -> transfer(source, target, amount, USER_ID))
                .isInstanceOf(WrongOperationException.class);
        }

        @Test
        void errorOnInsufficientBalance() {
            Card source = okCard();
            Card target = targetOkCard();
            prepareRepo(source, target);

            assertThatThrownBy(() -> transfer(source, target, 1000, USER_ID))
                .isInstanceOf(InsufficientBalanceException.class);
        }

        @Test
        void directionDetectAndBalanceFlow() {
            Card source = okCard();
            Card target = targetOkCard();
            prepareRepo(source, target);

            transfer(source, target, 20, USER_ID);
            assertThat(source.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(80));
            assertThat(target.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(120));

            // Обратно
            transfer(target, source, 10, USER_ID);
            assertThat(source.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(90));
            assertThat(target.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(110));
        }

        private void prepareRepo(Card source, Card target) {
            when(cardRepository.findAllByIdInAndOwnerIdOrderByIdAsc(
                argThat((list -> {
                    assertThat(list).containsExactlyInAnyOrder(source.getId(), target.getId());
                    return true;
                })),
                eq(source.getOwnerId()))
            ).thenReturn(List.of(source, target));
        }

        private Card targetOkCard() {
            Card card = okCard();
            card.setId(card.getId() + 1);
            return card;
        }

        private void transfer(Card source, Card target, Integer amount, Long requesterId) {
            userCardService.transfer(
                new TransferRequest(source.getId(), target.getId(), BigDecimal.valueOf(amount)),
                requesterId);
        }

        @Override
        public UserCardService getUserCardService() {
            return userCardService;
        }
    }

    @Nested
    class GetBalanceTest {
        @Test
        void cardNotFound() {
            when(cardRepository.findByIdAndOwnerId(eq(CARD_ID), eq(USER_ID + 1)))
                .thenReturn(Optional.empty());

            assertThatThrownBy(() -> userCardService.getBalance(CARD_ID, USER_ID + 1))
                .isInstanceOf(CardNotFoundException.class);
        }
    }

    @Nested
    class EnsureOperationsAllowedTest {

        @Test
        void errorBlocked() {
            Card card = okCard();
            card.setStatus(CardStatus.BLOCKED);

            assertThatThrownBy(() -> userCardService.ensureOperationsAllowed(card, USER_ID))
                .isInstanceOf(WrongOperationException.class);
        }

        @Test
        void errorExpired() {
            Card card = okCard();
            card.setExpiration(LocalDate.now().minusDays(1));

            assertThatThrownBy(() -> userCardService.ensureOperationsAllowed(card, USER_ID))
                .isInstanceOf(WrongOperationException.class);
        }

        @Test
        void errorNotOwner() {
            Card card = okCard();
            card.setOwnerId(USER_ID + 1);

            assertThatThrownBy(() -> userCardService.ensureOperationsAllowed(card, USER_ID))
                .isInstanceOf(CardNotFoundException.class);
        }

        @Test
        void operationsAllowed() {
            Card card = okCard();

            userCardService.ensureOperationsAllowed(card, USER_ID);
        }
    }

}
