package com.example.grouple.dto.joinrequest.response;

import com.example.grouple.entity.JoinRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class JoinRequestResponse {
    private final Integer id;
    private final Integer organizationId;
    private final Integer userId;
    private final String username;
    private final String status;
    private final String message;
    private final String decisionReason;
    private final Instant createdAt;
    private final Instant decidedAt;

    public static JoinRequestResponse from(JoinRequest joinRequest) {
        return JoinRequestResponse.builder()
                .id(joinRequest.getId())
                .organizationId(joinRequest.getOrganization() != null ? joinRequest.getOrganization().getId() : null)
                .userId(joinRequest.getUser() != null ? joinRequest.getUser().getId() : null)
                .username(joinRequest.getUser() != null ? joinRequest.getUser().getUsername() : null)
                .status(joinRequest.getStatus() != null ? joinRequest.getStatus().name() : null)
                .message(joinRequest.getMessage())
                .decisionReason(joinRequest.getDecisionReason())
                .createdAt(joinRequest.getCreatedAt())
                .decidedAt(joinRequest.getDecidedAt())
                .build();
    }
}
