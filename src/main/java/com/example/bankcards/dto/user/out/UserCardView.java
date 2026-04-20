package com.example.bankcards.dto.user.out;

import com.example.bankcards.entity.enums.CardStatus;

import java.time.LocalDate;

public record UserCardView(Long id, String maskedNumber, LocalDate expiration, CardStatus status) {}