package com.example.grouple.dto.document.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class DocumentReadDetailResponse {
    private Integer documentId;
    private String title;
    private String description;
    private String name;
    private String type;
    private Integer size;
    private String username;
    private Integer organizationId;
    private String organizationName;
    private Instant createdAt;
    private Instant updatedAt;
}
