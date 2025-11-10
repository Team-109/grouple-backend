package com.example.grouple.dto.organization.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class OrgMembersDeleteResponse {
    private final Integer organizationId;
    private final Integer memberId;
    private final String reason;
    private final Instant deletedAt;
}
