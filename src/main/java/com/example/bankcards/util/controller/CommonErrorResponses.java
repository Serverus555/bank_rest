package com.example.bankcards.util.controller;

import com.example.bankcards.dto.errorresponses.DtoError;
import com.example.bankcards.dto.errorresponses.ParamError;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ApiResponses({
    @ApiResponse(responseCode = "200"),
    @ApiResponse(
        responseCode = "400",
        content = @Content(schema = @Schema(oneOf = {DtoError.class, ParamError.class}))
    ),
    @ApiResponse(
        responseCode = "401",
        content = @Content
    ),
    @ApiResponse(
        responseCode = "403",
        content = @Content
    ),
    @ApiResponse(
        responseCode = "500",
        content = @Content(
            mediaType = MediaType.TEXT_PLAIN_VALUE,
            schema = @Schema(implementation = String.class))
    )
})
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommonErrorResponses {
}
