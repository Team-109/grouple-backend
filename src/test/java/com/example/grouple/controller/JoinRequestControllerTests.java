package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.joinrequest.request.JoinRequestCreateRequest;
import com.example.grouple.dto.joinrequest.response.JoinRequestResponse;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.JoinRequestService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JoinRequestControllerTests {

    @Mock
    private JoinRequestService joinRequestService;
    @InjectMocks
    private JoinRequestController controller;

    @Test
    void shouldCreateJoinRequest() {
        JoinRequestCreateRequest request = new JoinRequestCreateRequest();
        request.setMessage("가입 요청합니다.");

        JoinRequestResponse response = JoinRequestResponse.builder()
                .id(1)
                .organizationId(2)
                .userId(10)
                .username("tester")
                .status("PENDING")
                .message("가입 요청합니다.")
                .createdAt(Instant.now())
                .build();

        when(joinRequestService.createJoinRequest(10, "ABC123", request)).thenReturn(response);

        ResponseEntity<?> result = controller.createJoinRequest(new AuthPrincipal(10, "tester"),
                "ABC123", request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(response);
        verify(joinRequestService).createJoinRequest(10, "ABC123", request);
    }
}
