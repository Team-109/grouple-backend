package com.example.grouple.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class UserInfoResponse {
    private Integer id;
    private String username;
    private String email;
    private String phone;
    private String image;
    private Instant createdAt;
    private Instant updatedAt;
}
