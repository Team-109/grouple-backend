package com.example.grouple.dto.organization.response;

import com.example.grouple.entity.Organization;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class OrgDetailResponse {
    private Integer id;
    private String name;
    private String description;
    private String category;
    private String image_url;
    private String code;
    private Integer owner_id;
    private Instant createdAt;
    private Instant updatedAt;

    public static OrgDetailResponse from(Organization organization) {
        return OrgDetailResponse.builder()
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
