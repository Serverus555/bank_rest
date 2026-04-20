package com.example.bankcards.service.admin;

import com.example.bankcards.dto.admin.in.CreateUserRequest;
import com.example.bankcards.dto.admin.in.EditUserRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.user.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    private static final String PASSWORD = "password";
    private static final String ENCRYPTED_PASSWORD = "encrypted";

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminUserService adminUserService;


    @Test
    void createPasswordEncrypted() {
        prepareEncoder();

        adminUserService.create(new CreateUserRequest("user", PASSWORD, Role.USER.name()));
        verify(userRepository).save(argThat(user -> user.getEncryptedPassword().equals(ENCRYPTED_PASSWORD)));
    }

    @Test
    void editUserNotFound() {
        when(userRepository.findById(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminUserService.edit(new EditUserRequest(1L, PASSWORD)))
            .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void editedPasswordEncrypted() {
        prepareEncoder();
        User user = new User(1L, "user", "old", Role.USER);
        when(userRepository.findById(any()))
            .thenReturn(Optional.of(user));

        adminUserService.edit(new EditUserRequest(1L, PASSWORD));

        verify(userRepository).save(argThat(saved -> saved.getEncryptedPassword().equals(ENCRYPTED_PASSWORD)));
    }

    private void prepareEncoder() {
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCRYPTED_PASSWORD);
    }
}
