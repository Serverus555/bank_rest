package com.example.bankcards.dto.errorresponses;

import java.util.List;
import java.util.Map;

/**
    {fields:
        {field1: [msg1, msg2], ...},
    otherErrors: [msg3, msg4, ...]}
*/
public record DtoError(
    Map<String, List<String>> fields,
    List<String> otherErrors
) {}
