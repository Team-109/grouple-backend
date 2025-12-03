package com.example.grouple.dto.organization.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class OrgDeleteResponse {
    private Integer id;
    private String code;
    private Instant deletedAt;
}
