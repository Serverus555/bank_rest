package com.example.bankcards.controller;

import com.example.bankcards.security.LoginService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    private static final String LOGIN_PATH = "/auth/login";
    private static final String USERNAME = "user";
    private static final String TOKEN = "token";
    private static final String SCOPE = "ROLE_USER";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginService loginService;

    @Nested
    class LoginTest {

        @Test
        void ok() throws Exception {
            String password = "password";

            when(loginService.login(USERNAME, password)).thenReturn(jwt());

            mockMvc.perform(post(LOGIN_PATH)
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .params(loginParams(password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value(TOKEN))
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                // 3600 - час, expires_in это промежуток с issuedAt ДО expiresAt, поэтому -1 секунда
                .andExpect(jsonPath("$.expires_in").value(3599))
                .andExpect(jsonPath("$.scope").value(SCOPE));
        }

        @Test
        void badCredentials() throws Exception {
            String wrongPassword = "wrong";
            String badCredentialsMessage = "Неверные учётные данные";

            when(loginService.login(USERNAME, wrongPassword)).thenThrow(new BadCredentialsException("bad credentials"));

            mockMvc.perform(post(LOGIN_PATH)
                    .contentType(APPLICATION_FORM_URLENCODED)
                    .params(loginParams(wrongPassword)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(badCredentialsMessage));
        }
    }

    private static Jwt jwt() {
        Instant issuedAt = Instant.now();
        // Час
        Instant expiresAt = issuedAt.plusSeconds(3600);

        return Jwt.withTokenValue(TOKEN)
            .header("alg", "none")
            .issuedAt(issuedAt)
            .expiresAt(expiresAt)
            .claim("scp", List.of(SCOPE))
            .build();
    }

    private static MultiValueMap<String, String> loginParams(String password) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", USERNAME);
        params.add("password", password);
        return params;
    }
}
