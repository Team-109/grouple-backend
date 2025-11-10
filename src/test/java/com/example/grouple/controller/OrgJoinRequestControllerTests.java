package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.joinrequest.request.JoinRequestDecisionRequest;
import com.example.grouple.dto.joinrequest.response.JoinRequestListResponse;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrgJoinRequestControllerTests {

    @Mock
    private JoinRequestService joinRequestService;
    @InjectMocks
    private OrgJoinRequestController controller;

    @Test
    void shouldReturnJoinRequestList() {
        JoinRequestResponse entry = JoinRequestResponse.builder()
                .id(1)
                .organizationId(2)
                .userId(10)
                .username("tester")
                .status("PENDING")
                .createdAt(Instant.now())
                .build();
        JoinRequestListResponse response = JoinRequestListResponse.of(2, List.of(entry));
        when(joinRequestService.getOrganizationJoinRequests(15, 2)).thenReturn(response);

        ResponseEntity<?> result = controller.getJoinRequests(new AuthPrincipal(15, "owner"), 2);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(response);
        verify(joinRequestService).getOrganizationJoinRequests(15, 2);
    }

    @Test
    void shouldReturnJoinRequestDetail() {
        JoinRequestResponse response = JoinRequestResponse.builder()
                .id(5)
                .organizationId(2)
                .userId(99)
                .username("applicant")
                .status("PENDING")
                .createdAt(Instant.now())
                .build();
        when(joinRequestService.getOrganizationJoinRequest(15, 2, 5)).thenReturn(response);

        ResponseEntity<?> result = controller.getJoinRequest(new AuthPrincipal(15, "owner"), 2, 5);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(response);
        verify(joinRequestService).getOrganizationJoinRequest(15, 2, 5);
    }

    @Test
    void shouldApproveJoinRequest() {
        JoinRequestDecisionRequest request = new JoinRequestDecisionRequest();
        request.setReason("환영합니다");

        JoinRequestResponse response = JoinRequestResponse.builder()
                .id(5)
                .organizationId(2)
                .userId(99)
                .username("applicant")
                .status("APPROVED")
                .decidedAt(Instant.now())
                .build();
        when(joinRequestService.approveJoinRequest(15, 2, 5, request)).thenReturn(response);

        ResponseEntity<?> result = controller.approveJoinRequest(new AuthPrincipal(15, "owner"),
                2, 5, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(response);
        verify(joinRequestService).approveJoinRequest(15, 2, 5, request);
    }

    @Test
    void shouldRejectJoinRequest() {
        JoinRequestDecisionRequest request = new JoinRequestDecisionRequest();
        request.setReason("정원이 가득 찼습니다.");

        JoinRequestResponse response = JoinRequestResponse.builder()
                .id(5)
                .organizationId(2)
                .userId(99)
                .username("applicant")
                .status("REJECTED")
                .decisionReason("정원이 가득 찼습니다.")
                .decidedAt(Instant.now())
                .build();
        when(joinRequestService.rejectJoinRequest(15, 2, 5, request)).thenReturn(response);

        ResponseEntity<?> result = controller.rejectJoinRequest(new AuthPrincipal(15, "owner"),
                2, 5, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        Assertions.assertNotNull(body);
        assertThat(body.getData()).isEqualTo(response);
        verify(joinRequestService).rejectJoinRequest(15, 2, 5, request);
    }
}
