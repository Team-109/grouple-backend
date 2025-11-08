package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.user.request.UserDeleteRequest;
import com.example.grouple.dto.user.request.UserImageModifyForm;
import com.example.grouple.dto.user.request.UserModifyRequest;
import com.example.grouple.dto.auth.response.UserInfoResponse;
import com.example.grouple.dto.user.response.UserImageModifyResponse;
import com.example.grouple.dto.user.response.UserModifyResponse;
import com.example.grouple.service.AuthService;
import com.example.grouple.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTests {

    @Mock
    private UserService userService;
    @Mock
    private AuthService authService;
    @InjectMocks
    private UserController controller;

    @Test
    void shouldReturnCurrentUserInfo() throws Exception {
        UserInfoResponse response = UserInfoResponse.builder()
                .id(1)
                .username("tester")
                .email("tester@example.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        when(userService.getUserById(1)).thenReturn(response);

        ResponseEntity<?> result = controller.getUserInfo(1);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getData()).isEqualTo(response);
        verify(userService).getUserById(1);
    }

    @Test
    void shouldUpdateUserInfo() throws Exception {
        UserModifyRequest request = new UserModifyRequest();
        UserModifyResponse response = UserModifyResponse.builder()
                .id(1)
                .username("tester")
                .email("tester@example.com")
                .updatedAt(Instant.now())
                .build();
        when(userService.update(1, request)).thenReturn(response);

        ResponseEntity<?> result = controller.updateUserInfo(1, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getData()).isEqualTo(response);
        verify(userService).update(1, request);
    }

    @Test
    void shouldUpdateUserPhoto() throws Exception {
        UserImageModifyForm form = new UserImageModifyForm();
        form.setImage("profile.png");
        UserImageModifyResponse response = UserImageModifyResponse.builder()
                .image("profile.png")
                .updatedAt(Instant.now())
                .build();
        when(userService.updateImage(1, form)).thenReturn(response);

        ResponseEntity<?> result = controller.updateUserPhoto(1, form);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getData()).isEqualTo(response);
        verify(userService).updateImage(1, form);
    }

    @Test
    void shouldDeleteUser() throws Exception {
        UserDeleteRequest request = new UserDeleteRequest();

        ResponseEntity<?> result = controller.deleteUser(1, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getMessage()).contains("삭제");
        verify(userService).deleteUser(1, request);
    }
}
