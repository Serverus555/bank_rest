package com.example.bankcards.controller.user;

import com.example.bankcards.config.security.SecurityConfig;
import com.example.bankcards.dto.mappers.UserDtoMappers;
import com.example.bankcards.dto.user.in.TransferRequest;
import com.example.bankcards.service.user.UserCardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.cors.CorsConfigurationSource;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserCardController.class)
@Import(SecurityConfig.class)
class UserCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private UserCardService userCardService;

    @MockitoBean
    private UserDtoMappers userDtoMappers;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private CorsConfigurationSource corsConfigurationSource;

    @Nested
    class TransferTest {

        private static final String TRANSFER_PATH = "/user/card/transfer";
        private static final Long USER_ID = 15L;
        private static final Long SOURCE_CARD_ID = 1L;
        private static final BigDecimal TRANSFER_AMOUNT = new BigDecimal("10.50");

        @Test
        void ok() throws Exception {
            Long targetCardId = 2L;
            TransferRequest request = new TransferRequest(SOURCE_CARD_ID, targetCardId, TRANSFER_AMOUNT);

            mockMvc.perform(post(TRANSFER_PATH)
                    .with(userJwt(USER_ID))
                    .contentType(APPLICATION_JSON)
                    .content(json(request)))
                .andExpect(status().isOk());

            verify(userCardService).transfer(argThat(actual -> actual.source().equals(SOURCE_CARD_ID)
                && actual.target().equals(targetCardId)
                && actual.amount().compareTo(TRANSFER_AMOUNT) == 0), eq(USER_ID));
        }

        @Test
        void validationError() throws Exception {
            String differentCardsMessage = "Карты должны быть разными";
            TransferRequest request = new TransferRequest(SOURCE_CARD_ID, SOURCE_CARD_ID, TRANSFER_AMOUNT);

            mockMvc.perform(post(TRANSFER_PATH)
                    .with(userJwt(USER_ID))
                    .contentType(APPLICATION_JSON)
                    .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.otherErrors[0]").value(differentCardsMessage));

            verifyNoInteractions(userCardService);
        }
    }

    @Nested
    class GetBalanceTest {

        @Test
        void ok() throws Exception {
            Long userId = 15L;
            Long cardId = 7L;
            BigDecimal balance = new BigDecimal("123.45");

            when(userCardService.getBalance(cardId, userId)).thenReturn(balance);

            mockMvc.perform(get("/user/card/balance")
                    .with(userJwt(userId))
                    .param("cardId", String.valueOf(cardId)))
                .andExpect(status().isOk())
                .andExpect(content().string(balance.toString()));
        }
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private static RequestPostProcessor userJwt(Long userId) {
        return jwt()
            .jwt(jwt -> jwt.subject(String.valueOf(userId)))
            .authorities(new SimpleGrantedAuthority("SCOPE_ROLE_USER"));
    }
}
