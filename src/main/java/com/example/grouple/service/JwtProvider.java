package com.example.grouple.service;

import com.example.grouple.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {
    private final SecretKey jwtKey;
    private final JwtParser parser;
    public JwtProvider(SecretKey jwtKey) {
        this.jwtKey = jwtKey;
        this.parser = Jwts.parserBuilder().setSigningKey(jwtKey).build();
    }
    /**
     * JWT 토큰 생성
     * @param user 유저 객체
     * @param expiration 토큰 만료 시간 (밀리초)
     * @return JWT 문자열
     */
    String generateToken(User user, long expiration) {
        return Jwts.builder()
                .setSubject(user.getUsername()) // 토큰 소유자 usename
                .claim("id", user.getId())// 토큰 소유자 id
                .setIssuedAt(new Date()) // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 만료 시간
                .signWith(jwtKey, SignatureAlgorithm.HS256) // 서명 알고리즘 및 비밀키
                .compact();
    }

    Claims parseClaims(String token) {
        return parser.parseClaimsJws(token).getBody();
    }
}
