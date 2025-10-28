package com.example.grouple.dto.user.response;

import lombok.*;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class UserModifyResponse {
    private Integer id;
    private String username;
    private String email;
    private String phone;
    private String image;
    private Instant createdAt;
    private Instant updatedAt;
}
