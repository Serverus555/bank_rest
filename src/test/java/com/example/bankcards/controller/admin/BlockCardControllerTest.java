package com.example.bankcards.controller.admin;

import com.example.bankcards.config.security.SecurityConfig;
import com.example.bankcards.dto.admin.out.AdminCardView;
import com.example.bankcards.dto.admin.out.PendingBlockCardView;
import com.example.bankcards.dto.mappers.AdminDtoMappers;
import com.example.bankcards.entity.BlockCardRequest;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.service.admin.AdminBlockCardService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BlockCardController.class)
@Import(SecurityConfig.class)
class BlockCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminBlockCardService adminBlockCardService;

    @MockitoBean
    private AdminDtoMappers adminDtoMappers;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private CorsConfigurationSource corsConfigurationSource;

    @Nested
    class GetPendingBlockRequestsTest {

        @Test
        void ok() throws Exception {
            Long requestId = 3L;
            Long cardId = 10L;
            Long ownerId = 5L;
            String maskedNumber = "**** **** **** 1234";
            String ownerText = "IVAN IVANOV";
            LocalDate expiration = LocalDate.of(2030, 1, 1);

            BlockCardRequest request = BlockCardRequest.builder()
                .id(requestId)
                .completed(false)
                .build();

            PendingBlockCardView view = new PendingBlockCardView(
                requestId,
                new AdminCardView(cardId, maskedNumber, ownerId, ownerText, expiration, CardStatus.ACTIVE)
            );

            when(adminBlockCardService.getPendingBlockRequests(any()))
                .thenReturn(new PageImpl<>(List.of(request)));
            when(adminDtoMappers.pendingBlockCardView(request)).thenReturn(view);

            mockMvc.perform(get("/admin/card/block/pending")
                    .with(adminJwt())
                    .param("page", "0")
                    .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(requestId))
                .andExpect(jsonPath("$.content[0].card.id").value(cardId));

            verify(adminBlockCardService).getPendingBlockRequests(any());
        }
    }

    @Nested
    class BlockTest {

        private static final String BLOCK_PATH = "/admin/card/block/block";

        @Test
        void cardNotFound() throws Exception {
            Long missingCardId = 15L;
            String cardNotFoundMessage = "Карта не найдена";

            doThrow(new CardNotFoundException()).when(adminBlockCardService).block(missingCardId);

            mockMvc.perform(post(BLOCK_PATH)
                    .with(adminJwt())
                    .param("cardId", String.valueOf(missingCardId)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(cardNotFoundMessage));
        }

        @Test
        void ok() throws Exception {
            Long blockedCardId = 7L;

            mockMvc.perform(post(BLOCK_PATH)
                    .with(adminJwt())
                    .param("cardId", String.valueOf(blockedCardId)))
                .andExpect(status().isOk());

            verify(adminBlockCardService).block(blockedCardId);
        }
    }

    private static RequestPostProcessor adminJwt() {
        return jwt()
            .jwt(jwt -> jwt.subject("1"))
            .authorities(new SimpleGrantedAuthority("SCOPE_ROLE_ADMIN"));
    }
}
