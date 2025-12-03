package com.example.grouple.service;

import com.example.grouple.dto.auth.request.LoginRequest;
import com.example.grouple.dto.auth.response.LoginResponse;
import com.example.grouple.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtProvider jwt;
    private static final long ACCESS_EXP = 1000 * 60 * 15;
    private static final long REFRESH_EXP = 1000 * 60 * 60 * 24;

    public AuthService(UserRepository r, PasswordEncoder e, JwtProvider j){ this.repo=r; this.encoder=e; this.jwt=j; }

    public LoginResponse login(LoginRequest req) throws Exception {
        var user = repo.findByUsername(req.getUsername()).orElseThrow(() -> new Exception("아이디 또는 비밀번호가 올바르지 않습니다."));
        if (!encoder.matches(req.getPassword(), user.getPassword()))
            throw new Exception("아이디 또는 비밀번호가 올바르지 않습니다.");
        return new LoginResponse(
                jwt.generateToken(user, ACCESS_EXP),
                jwt.generateToken(user, REFRESH_EXP)
        );
    }

    public LoginResponse refresh(String refreshToken) throws Exception {
        try {
            var claims = jwt.parseClaims(refreshToken);
            Integer userId = claims.get("id", Integer.class);
            if (userId == null) {
                throw new Exception("리프레시 토큰에 사용자 정보가 없습니다.");
            }
            var user = repo.findById(userId)
                    .orElseThrow(() -> new Exception("해당 사용자를 찾을 수 없습니다."));
            return new LoginResponse(
                    jwt.generateToken(user, ACCESS_EXP),
                    jwt.generateToken(user, REFRESH_EXP)
            );
        } catch (JwtException e) {
            throw new Exception("유효하지 않은 리프레시 토큰입니다.", e);
        }
    }
}
