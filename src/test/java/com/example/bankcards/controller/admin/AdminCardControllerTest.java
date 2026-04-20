package com.example.bankcards.controller.admin;

import com.example.bankcards.config.security.SecurityConfig;
import com.example.bankcards.dto.admin.in.CreateCardRequest;
import com.example.bankcards.dto.admin.out.AdminCardView;
import com.example.bankcards.dto.mappers.AdminDtoMappers;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.service.admin.AdminCardService;
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
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCardController.class)
@Import(SecurityConfig.class)
class AdminCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private AdminCardService adminCardService;

    @MockitoBean
    private AdminDtoMappers adminDtoMappers;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private CorsConfigurationSource corsConfigurationSource;

    @Nested
    class CreateTest {

        private static final String CREATE_PATH = "/admin/card";
        private static final Long CARD_ID = 1L;
        private static final Long OWNER_ID = 10L;
        private static final String OWNER_TEXT = "IVAN IVANOV";
        private static final String MASKED_NUMBER = "**** **** **** 1234";
        private static final LocalDate EXPIRATION = LocalDate.of(2030, 1, 1);
        private static final BigDecimal BALANCE = new BigDecimal("100.00");

        @Test
        void ok() throws Exception {
            Card card = Card.builder()
                .id(CARD_ID)
                .ownerId(OWNER_ID)
                .ownerText(OWNER_TEXT)
                .expiration(EXPIRATION)
                .status(CardStatus.ACTIVE)
                .balance(BALANCE)
                .build();

            AdminCardView view = new AdminCardView(CARD_ID, MASKED_NUMBER, OWNER_ID, OWNER_TEXT, EXPIRATION, CardStatus.ACTIVE);
            CreateCardRequest request = createCardRequest();

            when(adminCardService.create(any())).thenReturn(card);
            when(adminDtoMappers.adminCardView(card)).thenReturn(view);

            mockMvc.perform(post(CREATE_PATH)
                    .with(adminJwt())
                    .contentType(APPLICATION_JSON)
                    .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CARD_ID))
                .andExpect(jsonPath("$.maskedNumber").value(MASKED_NUMBER))
                .andExpect(jsonPath("$.ownerId").value(OWNER_ID))
                .andExpect(jsonPath("$.status").value(CardStatus.ACTIVE.name()));

            verify(adminCardService).create(argThat(actual -> actual.ownerId().equals(OWNER_ID)
                && actual.ownerText().equals(OWNER_TEXT)
                && actual.expiration().equals(EXPIRATION)
                && actual.balance().compareTo(BALANCE) == 0));
        }

        private CreateCardRequest createCardRequest() {
            return new CreateCardRequest(OWNER_ID, OWNER_TEXT, EXPIRATION, BALANCE);
        }
    }

    @Nested
    class ActivateTest {

        @Test
        void cardNotFound() throws Exception {
            Long missingCardId = 15L;
            String cardNotFoundMessage = "Карта не найдена";

            doThrow(new CardNotFoundException()).when(adminCardService).activate(missingCardId);

            mockMvc.perform(post("/admin/card/activate")
                    .with(adminJwt())
                    .param("id", String.valueOf(missingCardId)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(cardNotFoundMessage));
        }
    }

    @Nested
    class DeleteTest {

        @Test
        void ok() throws Exception {
            Long deletedCardId = 7L;

            mockMvc.perform(delete("/admin/card")
                    .with(adminJwt())
                    .param("id", String.valueOf(deletedCardId)))
                .andExpect(status().isOk());

            verify(adminCardService).delete(deletedCardId);
        }
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private static RequestPostProcessor adminJwt() {
        return jwt()
            .jwt(jwt -> jwt.subject("1"))
            .authorities(new SimpleGrantedAuthority("SCOPE_ROLE_ADMIN"));
    }

    private static RequestPostProcessor userJwt() {
        return jwt()
            .jwt(jwt -> jwt.subject("1"))
            .authorities(new SimpleGrantedAuthority("SCOPE_ROLE_USER"));
    }
}
