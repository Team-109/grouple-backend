package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.auth.request.LoginRequest;
import com.example.grouple.dto.auth.request.RegisterRequest;
import com.example.grouple.dto.auth.response.LoginResponse;
import com.example.grouple.dto.auth.response.RegisterResponse;
import com.example.grouple.dto.auth.response.UserInfoResponse;
import com.example.grouple.service.AuthService;
import com.example.grouple.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

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
    void shouldRegisterUser() throws Exception {
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
        assertThat(body.getData()).isEqualTo(response);
        verify(userService).register(request);
    }

    @Test
    void shouldReturnMessageWhenUsernameAvailable() throws Exception {
        when(userService.existsByUsername("tester")).thenReturn(false);

        ResponseEntity<?> result = authController.checkIdAvailable("tester");

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getMessage()).contains("available");
        verify(userService).existsByUsername("tester");
    }

    @Test
    void shouldThrowWhenUsernameTaken() {
        when(userService.existsByUsername("taken")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> authController.checkIdAvailable("taken"));
    }

    @Test
    void shouldLoginUser() throws Exception {
        LoginRequest request = new LoginRequest();
        LoginResponse response = new LoginResponse("access", "refresh");
        when(authService.login(request)).thenReturn(response);

        ResponseEntity<?> result = authController.login(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getData()).isEqualTo(response);
        verify(authService).login(request);
    }

    @Test
    void shouldReturnSuccessWhenFetchingMe() throws Exception {
        UserInfoResponse info = UserInfoResponse.builder()
                .id(1)
                .username("tester")
                .email("tester@example.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        when(userService.getUserById(1)).thenReturn(info);

        ResponseEntity<?> result = authController.getUserInfo(1);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getMessage()).isEqualTo("Authenticated");
        verify(userService).getUserById(1);
    }
}
