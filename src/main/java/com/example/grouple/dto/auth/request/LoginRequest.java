package com.example.grouple.dto.auth.request;

import lombok.*;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
