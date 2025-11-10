package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.organization.response.OrgListResponse;
import com.example.grouple.dto.user.request.UserDeleteRequest;
import com.example.grouple.dto.user.request.UserImageModifyForm;
import com.example.grouple.dto.user.request.UserModifyRequest;
import com.example.grouple.dto.auth.response.UserInfoResponse;
import com.example.grouple.dto.user.response.UserImageModifyResponse;
import com.example.grouple.dto.user.response.UserModifyResponse;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.OrganizationService;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTests {

    @Mock
    private UserService userService;
    @Mock
    private OrganizationService organizationService;
    @InjectMocks
    private UserController controller;

    @Test
    void shouldReturnCurrentUserInfo() {
        UserInfoResponse response = UserInfoResponse.builder()
                .id(1)
                .username("tester")
                .email("tester@example.com")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        when(userService.getUserById(1)).thenReturn(response);

        ResponseEntity<?> result = controller.getUserInfo(new AuthPrincipal(1, "tester"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
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

        ResponseEntity<?> result = controller.updateUserInfo(new AuthPrincipal(1, "tester"), request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
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

        ResponseEntity<?> result = controller.updateUserPhoto(new AuthPrincipal(1, "tester"), form);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(response);
        verify(userService).updateImage(1, form);
    }

    @Test
    void shouldDeleteUser() throws Exception {
        UserDeleteRequest request = new UserDeleteRequest();

        ResponseEntity<?> result = controller.deleteUser(new AuthPrincipal(1, "tester"), request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getMessage()).contains("삭제");
        verify(userService).deleteUser(1, request);
    }

    @Test
    void shouldReturnOrganizationsOwnedByCurrentUser() {
        OrgListResponse entry = OrgListResponse.builder()
                .id(42)
                .name("Org")
                .description("Desc")
                .category("CAT")
                .image_url("logo.png")
                .code("ABC123")
                .owner_id(15)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        List<OrgListResponse> expected = List.of(entry);
        when(organizationService.getOrgsByOwner_Id(15)).thenReturn(expected);

        ResponseEntity<?> result = controller.getUserOrganizations(new AuthPrincipal(15, "owner"));

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(expected);
        verify(organizationService).getOrgsByOwner_Id(15);
    }
}
