package com.example.grouple.dto.document.request;

import lombok.*;

@Data
public class DocumentUpdateRequest {
    private Integer documentId;
    private Integer organizationId;
    private String title;
    private String description;
    private String name;
    private String type;
    private Integer size;
}
