package com.example.bankcards.dto.admin.in;


import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateCardRequest(
    @NotNull
    Long ownerId,

    @NotBlank
    @Size(max = 128)
    String ownerText,

    @NotNull
//    @Future не нужен - админ знает что делает
    LocalDate expiration,

    @NotNull
    @Digits(integer = 10, fraction = 2)
    // В теории может быть отрицательным (Допустим долг за выпуск карты)
    BigDecimal balance
) {}

