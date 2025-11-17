package com.example.grouple.dto.announcement.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class AnnouncementCreateResponse {
    private Integer id;
    private String title;
    private String description;
    private Instant createdAt;
    private Boolean star;
    private Integer userId;
    private Integer organizationId;
}
