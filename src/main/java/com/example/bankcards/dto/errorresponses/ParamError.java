package com.example.bankcards.dto.errorresponses;

import java.util.Map;

public record ParamError(
    Map<String, String> errors
) {}
