package com.example.grouple.dto.organization.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class OrgMembersResponse {
    private final Integer organizationId;
    private final int totalCount;
    private final List<MemberInfo> members;

    public static OrgMembersResponse of(Integer orgId, List<MemberInfo> members) {
        return OrgMembersResponse.builder()
                .organizationId(orgId)
                .totalCount(members.size())
                .members(members)
                .build();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MemberInfo {
        private final Integer memberId;
        private final String username;
        private final String email;
        private final String role;
        private final Instant joinedAt;
    }
}
