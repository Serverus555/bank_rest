package com.example.bankcards.dto.admin.out;

import com.example.bankcards.entity.enums.CardStatus;

import java.time.LocalDate;

public record AdminCardView(Long id, String maskedNumber, Long ownerId, String ownerText, LocalDate expiration, CardStatus status) {}
