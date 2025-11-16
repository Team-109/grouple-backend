package com.example.grouple.dto.document.request;

import lombok.*;

@Data
public class DocumentCreateRequest {
    private String title;
    private String description;
    private String name;
    private String type;
    private Integer size;
    private Integer organizationId;
}