package com.example.grouple.dto.organization.request;

import lombok.Data;

@Data
public class OrgCreateRequest {
    private String name;
    private String description;
    private String category;
    private String image_url;
}

