package com.example.grouple.dto.organization.response;

import com.example.grouple.entity.Organization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class OrgListResponse {
    private final Integer id;
    private final String name;
    private final String description;
    private final String category;
    private final String image_url;
    private final String code;
    private final Integer owner_id;
    private final Instant createdAt;
    private final Instant updatedAt;

    public static OrgListResponse from(Organization organization) {
        return OrgListResponse.builder()
                .id(organization.getId())
                .name(organization.getName())
                .description(organization.getDescription())
                .category(organization.getCategory())
                .image_url(organization.getImage())
                .code(organization.getCode())
                .owner_id(organization.getOwner() != null ? organization.getOwner().getId() : null)
                .createdAt(organization.getCreatedAt())
                .updatedAt(organization.getUpdatedAt())
                .build();
    }
}
