package com.example.grouple.service;

import com.example.grouple.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTests {

    @Test
    void shouldGenerateAndParseToken() {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        JwtProvider provider = new JwtProvider(key);
        User user = new User();
        user.setId(1);
        user.setUsername("tester");

        String token = provider.generateToken(user, 1000);
        Claims claims = provider.parseClaims(token);

        assertThat(claims.getSubject()).isEqualTo("tester");
        assertThat(claims.get("id", Integer.class)).isEqualTo(1);
    }
}
