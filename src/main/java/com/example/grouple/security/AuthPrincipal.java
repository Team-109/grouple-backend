package com.example.grouple.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthPrincipal {
    private final Integer id;
    private final String username;
}
