package com.example.grouple.dto.document.request;

import lombok.*;

@Data
public class DocumentDeleteRequest {
    private Integer documentId;
    private Integer organizationId;
}
