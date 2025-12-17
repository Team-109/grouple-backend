package com.example.grouple.service;

import com.example.grouple.dto.auth.request.RegisterRequest;
import com.example.grouple.dto.auth.response.RegisterResponse;
import com.example.grouple.repository.UserRepository;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        userService = new UserService(userRepository, passwordEncoder, key);
    }

    @Test
    void register_shouldPersistUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user");
        request.setPassword("pass");
        request.setPasswordConfirm("pass");
        request.setEmail("e@example.com");
        request.setPhone("010-0000-0000");

        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        RegisterResponse response = userService.register(request);

        assertThat(response.getUsername()).isEqualTo("user");
        assertThat(response.getEmail()).isEqualTo("e@example.com");
    }

    @Test
    void register_shouldThrowWhenPasswordMismatch() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user");
        request.setPassword("pass");
        request.setPasswordConfirm("other");
        request.setEmail("e@example.com");
        request.setPhone("010-0000-0000");

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void register_shouldThrowOnDuplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("dup");
        request.setPassword("pass");
        request.setPasswordConfirm("pass");
        request.setEmail("e@example.com");
        request.setPhone("010-0000-0000");

        when(userRepository.existsByUsername("dup")).thenReturn(true);

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
