package com.example.grouple.dto.document.response;

import jakarta.persistence.Column;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class DocumentCreateResponse {

    private Integer documentId;
    private String title;
    private String description;
    private String name;
    private String type;
    private Integer size;
    private String username;
    private Integer organizationId;
    private Instant createdAt;
    private Instant updatedAt;
}
