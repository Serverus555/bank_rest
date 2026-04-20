package com.example.bankcards.dto.user.in;

import com.example.bankcards.util.controller.validate.CardsDifferentConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Objects;

@CardsDifferentConstraint
public record TransferRequest(
    @NotNull
    Long source,

    @NotNull
    Long target,

    @NotNull
    @Positive
    @Digits(integer = 10, fraction = 2)
    BigDecimal amount
) {

    @AssertTrue(message = "Карты должны быть разными")
    @Schema(hidden = true)
    public boolean isDifferentCards() {
        return !Objects.equals(source, target);
    }
}