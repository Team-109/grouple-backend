package com.example.grouple.dto.auth.request;

import lombok.*;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String phone;
    private String image;
    private String password;
    private String passwordConfirm;
}

