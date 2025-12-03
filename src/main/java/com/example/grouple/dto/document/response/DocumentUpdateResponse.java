package com.example.grouple.dto.document.response;
import com.example.grouple.entity.Document;


import java.time.Instant;

public record DocumentUpdateResponse(
        Integer documentId,
        String title,
        String description,
        String name,
        String type,
        Integer size,
        String username,
        Integer organizationId,
        Instant createdAt,
        Instant updatedAt
) {
    // 정적 팩토리 메서드 예시
    public static DocumentUpdateResponse from(Document document) {
        return new DocumentUpdateResponse(
                document.getId(),
                document.getTitle(),
                document.getDescription(),
                document.getName(),
                document.getType(),
                document.getSize(),
                document.getUser().getUsername(),
                document.getOrganization().getId(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }
}
