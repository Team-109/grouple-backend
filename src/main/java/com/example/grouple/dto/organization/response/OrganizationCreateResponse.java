package com.example.grouple.dto.organization.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class OrganizationCreateResponse {
    private Integer id;
    private String name;
    private String code;
    private Integer owner_id;
    private Instant createdAt;
}
