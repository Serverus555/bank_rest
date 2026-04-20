package com.example.bankcards.service.admin;

import com.example.bankcards.dto.admin.in.CreateUserRequest;
import com.example.bankcards.dto.admin.in.EditUserRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.user.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User create(CreateUserRequest createUserRequest) {
        User user = User.builder()
            .username(createUserRequest.username())
            .encryptedPassword(passwordEncoder.encode(createUserRequest.password()))
            .role(Role.valueOf(createUserRequest.role()))
            .build();

        return userRepository.save(user);
    }

    public Page<User> getAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    public User edit(EditUserRequest editUserRequest) {
        User edited = userRepository.findById(editUserRequest.id())
            .map(user -> applyEdit(user, editUserRequest))
            .orElseThrow(UserNotFoundException::new);

        return userRepository.save(edited);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    // В данный момент 1 редактируемое поле. Если надо больше, то через mapstruct
    private User applyEdit(User user, EditUserRequest editUserRequest) {
        user.setEncryptedPassword(passwordEncoder.encode(editUserRequest.password()));
        return user;
    }
}
