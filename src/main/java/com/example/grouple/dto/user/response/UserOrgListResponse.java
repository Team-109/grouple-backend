package com.example.grouple.dto.user.response;

import lombok.*;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class UserOrgListResponse {
    private Integer id;
    private String name;
    private String imageUrl;
    private String description;
    private Instant createdAt;
    private Notice notice;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Notice {
        private String title;
        private String content;
    }
}
