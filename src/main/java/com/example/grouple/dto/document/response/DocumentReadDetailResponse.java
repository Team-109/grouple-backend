package com.example.grouple.dto.document.response;

import com.example.grouple.entity.Document;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
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

    // Document 엔티티를 DTO로 변환하는 정적 메서드
    public static DocumentReadDetailResponse from(Document document) {
        return DocumentReadDetailResponse.builder()
                .documentId(document.getId())
                .title(document.getTitle())
                .description(document.getDescription())
                .name(document.getName())
                .type(document.getType())
                .size(document.getSize())
                .username(document.getUser().getUsername())
                .organizationId(document.getOrganization().getId())
                .organizationName(document.getOrganization().getName())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}
