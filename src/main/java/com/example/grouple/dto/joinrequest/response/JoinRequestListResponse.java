package com.example.grouple.dto.joinrequest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class JoinRequestListResponse {
    private final Integer organizationId;
    private final int totalCount;
    private final List<JoinRequestResponse> requests;

    public static JoinRequestListResponse of(Integer orgId, List<JoinRequestResponse> requests) {
        return JoinRequestListResponse.builder()
                .organizationId(orgId)
                .totalCount(requests.size())
                .requests(requests)
                .build();
    }
}
