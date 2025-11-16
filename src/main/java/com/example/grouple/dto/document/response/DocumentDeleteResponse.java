package com.example.grouple.dto.document.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class DocumentDeleteResponse {
    private Integer documentId;
    private String message;
}
