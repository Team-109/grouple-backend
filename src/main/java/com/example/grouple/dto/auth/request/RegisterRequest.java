package com.example.grouple.dto.auth.request;

import lombok.*;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String passwordConfirm;

    private String name;
    private String email;
    private String phone;

    private String image;
}

