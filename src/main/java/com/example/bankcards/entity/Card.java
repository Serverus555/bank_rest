package com.example.bankcards.entity;


import com.example.bankcards.entity.enums.CardStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String encryptedNumber;
    private String last4Digits;

    // Специально не делаю связь ManyToOne User - User сейчас содержит только данные авторизации, так что излишен тут. Но в БД он Foreign Key
    private Long ownerId;

    /**
     * Фамилия и имя на карте. Не подтягивается из User, т.к. возможны ситуации когда имя на карте отличается от имени юзера.
     *  Смена ФИО - на карте должно остаться старое, выпуск карты ребёнку, т.д
     */
    private String ownerText;
    private LocalDate expiration;

    @Enumerated(EnumType.STRING)
    private CardStatus status;

    private BigDecimal balance;

    public CardStatus calculateStatus() {
        return expiration.isAfter(LocalDate.now()) ? status : CardStatus.EXPIRED;
    }
}
