package com.example.grouple.dto;

import com.example.grouple.dto.joinrequest.request.JoinRequestCreateRequest;
import com.example.grouple.dto.joinrequest.request.JoinRequestDecisionRequest;
import com.example.grouple.dto.joinrequest.response.JoinRequestListResponse;
import com.example.grouple.dto.joinrequest.response.JoinRequestResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JoinRequestDtoTests {

    @Test
    void joinRequestCreateRequest_shouldHoldMessage() {
        JoinRequestCreateRequest req = new JoinRequestCreateRequest();
        req.setMessage("hi");
        assertThat(req.getMessage()).isEqualTo("hi");
    }

    @Test
    void joinRequestDecisionRequest_shouldHoldReason() {
        JoinRequestDecisionRequest req = new JoinRequestDecisionRequest();
        req.setReason("ok");
        assertThat(req.getReason()).isEqualTo("ok");
    }

    @Test
    void joinRequestResponse_shouldExposeFields() {
        Instant now = Instant.now();
        JoinRequestResponse res = JoinRequestResponse.builder()
                .id(1)
                .organizationId(2)
                .userId(3)
                .username("user")
                .status("PENDING")
                .message("msg")
                .decisionReason("reason")
                .createdAt(now)
                .decidedAt(now)
                .build();

        assertThat(res.getId()).isEqualTo(1);
        assertThat(res.getStatus()).isEqualTo("PENDING");
        assertThat(res.getDecisionReason()).isEqualTo("reason");
    }

    @Test
    void joinRequestListResponse_shouldHoldList() {
        JoinRequestResponse res = JoinRequestResponse.builder()
                .id(1)
                .organizationId(2)
                .status("PENDING")
                .build();
        JoinRequestListResponse list = JoinRequestListResponse.of(2, List.of(res));

        assertThat(list.getOrganizationId()).isEqualTo(2);
        assertThat(list.getRequests()).containsExactly(res);
    }
}
