package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;

// В идеале нужно доп шифрование пароля открытым ключом со стороны клиента - чтобы случайно по пути в сервис не засветить
//  (http сервер, trace логи, и т.д.)
public record LoginRequest(
    @NotBlank
    String username,
    @NotBlank
    String password
) {}
