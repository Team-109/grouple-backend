package com.example.grouple.service;

import com.example.grouple.dto.auth.request.LoginRequest;
import com.example.grouple.dto.auth.response.LoginResponse;
import com.example.grouple.entity.User;
import com.example.grouple.repository.UserRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtProvider jwtProvider;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtProvider);
    }

    @Test
    void login_shouldReturnTokensWhenPasswordMatches() throws Exception {
        User user = new User();
        user.setId(1);
        user.setUsername("tester");
        user.setPassword("hashed");
        LoginRequest request = new LoginRequest();
        request.setUsername("tester");
        request.setPassword("plain");

        when(userRepository.findByUsername("tester")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain", "hashed")).thenReturn(true);
        when(jwtProvider.generateToken(eq(user), anyLong())).thenReturn("access").thenReturn("refresh");

        LoginResponse response = authService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access");
        assertThat(response.getRefreshToken()).isEqualTo("refresh");
        verify(passwordEncoder).matches("plain", "hashed");
    }

    @Test
    void login_shouldThrowOnWrongPassword() {
        User user = new User();
        user.setId(1);
        user.setUsername("tester");
        user.setPassword("hashed");
        LoginRequest request = new LoginRequest();
        request.setUsername("tester");
        request.setPassword("plain");

        when(userRepository.findByUsername("tester")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plain", "hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(Exception.class);
    }

    @Test
    void refresh_shouldCreateNewTokensWhenRefreshValid() throws Exception {
        User user = new User();
        user.setId(2);
        user.setUsername("refresh");
        Claims claims = mock(Claims.class);

        when(jwtProvider.parseClaims("refresh-token")).thenReturn(claims);
        when(claims.get("id", Integer.class)).thenReturn(2);
        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(jwtProvider.generateToken(eq(user), anyLong())).thenReturn("new-access").thenReturn("new-refresh");

        LoginResponse response = authService.refresh("refresh-token");

        assertThat(response.getAccessToken()).isEqualTo("new-access");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh");
    }
}
