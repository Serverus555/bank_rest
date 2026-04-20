package com.example.bankcards.controller.admin;

import com.example.bankcards.config.security.SecurityConfig;
import com.example.bankcards.dto.admin.in.CreateUserRequest;
import com.example.bankcards.dto.admin.in.EditUserRequest;
import com.example.bankcards.dto.admin.out.AdminUserView;
import com.example.bankcards.dto.mappers.AdminDtoMappers;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.admin.AdminUserService;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@Import(SecurityConfig.class)
class AdminUserControllerTest {

    private static final String USER_PATH = "/admin/user";

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private AdminUserService adminUserService;

    @MockitoBean
    private AdminDtoMappers adminDtoMappers;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoBean
    private CorsConfigurationSource corsConfigurationSource;

    @Nested
    class EditTest {

        @Test
        void ok() throws Exception {
            Long userId = 5L;
            String username = "username";

            User user = User.builder()
                .id(userId)
                .username(username)
                .encryptedPassword("encoded")
                .role(Role.USER)
                .build();

            AdminUserView view = new AdminUserView(userId, username);
            EditUserRequest request = new EditUserRequest(userId, "new-password");

            when(adminUserService.edit(request)).thenReturn(user);
            when(adminDtoMappers.adminUserView(user)).thenReturn(view);

            mockMvc.perform(put(USER_PATH)
                    .with(adminJwt())
                    .contentType(APPLICATION_JSON)
                    .content(json(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.username").value(username));

            verify(adminUserService).edit(request);
        }
    }

    @Nested
    class CreateTest {

        @Test
        void validationError() throws Exception {
            CreateUserRequest request = new CreateUserRequest("", "password", Role.USER.name());

            mockMvc.perform(post(USER_PATH)
                    .with(adminJwt())
                    .contentType(APPLICATION_JSON)
                    .content(json(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.username[0]").exists());

            verifyNoInteractions(adminUserService);
        }
    }

    @Nested
    class DeleteTest {

        @Test
        void ok() throws Exception {
            Long deletedUserId = 7L;

            mockMvc.perform(delete(USER_PATH)
                    .with(adminJwt())
                    .param("id", String.valueOf(deletedUserId)))
                .andExpect(status().isOk());

            verify(adminUserService).delete(deletedUserId);
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
}
