package com.example.grouple.dto.user.request;

import lombok.*;

@Data
public class UserModifyRequest {
    private String username;
    private String email;
    private String phone;
    private String image;
    private String password;
}

