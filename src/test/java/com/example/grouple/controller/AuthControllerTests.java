package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.common.ConflictException;
import com.example.grouple.dto.auth.request.LoginRequest;
import com.example.grouple.dto.auth.request.RefreshTokenRequest;
import com.example.grouple.dto.auth.request.RegisterRequest;
import com.example.grouple.dto.auth.response.LoginResponse;
import com.example.grouple.dto.auth.response.RegisterResponse;
import com.example.grouple.dto.auth.response.UserInfoResponse;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.AuthService;
import com.example.grouple.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTests {

    @Mock
    private UserService userService;
    @Mock
    private AuthService authService;
    @InjectMocks
    private AuthController authController;

    @Test
    void shouldRegisterUser() {
        RegisterRequest request = new RegisterRequest();
        RegisterResponse response = RegisterResponse.builder()
                .id(1)
                .username("tester")
                .email("tester@example.com")
                .build();
        when(userService.register(request)).thenReturn(response);

        ResponseEntity<?> result = authController.registerUser(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(response);
        verify(userService).register(request);
    }

    @Test
    void shouldReturnMessageWhenUsernameAvailable() {
        when(userService.existsByUsername("tester")).thenReturn(false);

        ResponseEntity<?> result = authController.checkIdAvailable("tester");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getMessage()).contains("available");
        verify(userService).existsByUsername("tester");
    }

    @Test
    void shouldThrowWhenUsernameTaken() {
        when(userService.existsByUsername("taken")).thenReturn(true);

        assertThrows(ConflictException.class, () -> authController.checkIdAvailable("taken"));
    }

    @Test
    void shouldLoginUser() throws Exception {
        LoginRequest request = new LoginRequest();
        LoginResponse response = new LoginResponse("access", "refresh");
        when(authService.login(request)).thenReturn(response);

        ResponseEntity<?> result = authController.login(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(response);
        verify(authService).login(request);
    }

    @Test
    void shouldReturnSuccessWhenFetchingMe() {
        UserInfoResponse info = UserInfoResponse.builder()
                .id(1)
                .username("tester")
                .email("tester@example.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        when(userService.getUserById(1)).thenReturn(info);

        ResponseEntity<?> result = authController.getUserInfo(new AuthPrincipal(1, "tester"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getMessage()).isEqualTo("로그인된 사용자 아이디: tester");
        verify(userService).getUserById(1);
    }

    @Test
    void shouldRefreshTokens() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken("refresh");
        LoginResponse response = new LoginResponse("new-access", "new-refresh");
        when(authService.refresh("refresh")).thenReturn(response);

        ResponseEntity<?> result = authController.refresh(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(response);
        verify(authService).refresh("refresh");
    }
}
