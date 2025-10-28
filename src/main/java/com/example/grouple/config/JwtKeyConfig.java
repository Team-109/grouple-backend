// JwtKeyConfig.java
package com.example.grouple.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Base64;

@Configuration
public class JwtKeyConfig {
    @Bean
    public SecretKey jwtSigningKey(@Value("${app.jwt.secret-base64}") String b64) {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(b64));
    }
}