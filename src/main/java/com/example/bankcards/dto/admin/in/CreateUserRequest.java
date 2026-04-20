package com.example.bankcards.dto.admin.in;

import com.example.bankcards.entity.Role;
import com.example.bankcards.util.controller.validate.ValidateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank
    @Size(min = 5, max = 50)
    String username,

    @NotBlank
    @Size(min = 5, max = 50)
    String password,

    @ValidateEnum(target = Role.class)
    @Schema(implementation = Role.class)
    String role
) {}
