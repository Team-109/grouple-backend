package com.example.grouple.dto.document.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class DocumentReadListResponse {

    private List<DocumentSummary> documents; // 목록

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DocumentSummary {
        private Integer documentId;
        private String title;
        private String name;
        private String username;
        private Instant createdAt;
        private Instant updatedAt;
    }
}
