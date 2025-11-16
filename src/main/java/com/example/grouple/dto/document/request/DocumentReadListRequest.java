package com.example.grouple.dto.document.request;

import lombok.*;

@Data
public class DocumentReadListRequest {
    private Integer organizationId;
    private Integer userId;
}
