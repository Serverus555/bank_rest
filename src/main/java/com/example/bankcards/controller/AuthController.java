package com.example.bankcards.controller;

import com.example.bankcards.util.controller.CommonErrorResponses;
import com.example.bankcards.security.LoginService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.DefaultOAuth2AccessTokenResponseMapConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashSet;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Validated
@CommonErrorResponses
public class AuthController {

    private static final DefaultOAuth2AccessTokenResponseMapConverter TOKEN_CONVERTER = new DefaultOAuth2AccessTokenResponseMapConverter();

    private final LoginService loginService;

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Map<String, Object> login(@RequestParam @NotBlank String username,
                                     @RequestParam @NotBlank String password,
                                     // Не используем client id, secret, grant_type. Но нужны для api
                                     @RequestParam(value = "client_id", required = false) String clientId,
                                     @RequestParam(value = "client_secret", required = false) String clientSecret,
                                     @RequestParam(value = "grant_type", required = false) String grantType
    ) {
        Jwt token = loginService.login(username, password);
        return convert(token);
    }

    private static Map<String, Object> convert(Jwt jwt) {
        // Нельзя стандартными средствами просто конвертировать Jwt в формат спецификации
        //  По сути тут я поля перекладываю
        OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse.withToken(jwt.getTokenValue())
            .tokenType(OAuth2AccessToken.TokenType.BEARER)
            .expiresIn(Duration.between(jwt.getIssuedAt(), jwt.getExpiresAt()).getSeconds())
            .scopes(new HashSet<>(jwt.getClaimAsStringList("scp")))
            .build();

        return TOKEN_CONVERTER.convert(tokenResponse);
    }
}