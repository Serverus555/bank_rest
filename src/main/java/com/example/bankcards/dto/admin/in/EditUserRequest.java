package com.example.bankcards.dto.admin.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Сейчас у юзера только 1 редактируемое поле. Если будет больше, то можно использовать mapstruct.
 */
public record EditUserRequest(
    @NotNull
    Long id,

    @NotBlank
    @Size(min = 5, max = 50)
    String password
) {}
