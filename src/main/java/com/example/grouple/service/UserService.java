package com.example.grouple.service;

import com.example.grouple.dto.LoginRequest;
import com.example.grouple.dto.LoginResponse;
import com.example.grouple.dto.UserRequest;
import com.example.grouple.dto.UserResponse;
import com.example.grouple.entity.User;
import com.example.grouple.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    // 비밀번호 암호화 객체
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // JWT 발급을 위한 비밀키와 토큰 만료 시간
    private final String SECRET_KEY = "SecretKeyForJWT";
    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15분
    private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24; // 24시간

    public UserService(UserRepository repo) {
        this.userRepository = repo;
    }

    /**
     * 아이디 중복 체크
     * 회원가입 시 동일 ID 존재 여부 확인
     */
    public boolean existsById(String Id) {
        return userRepository.existsById(Id);
    }

    /**
     * 회원가입 처리
     * - UserRequest에서 전달받은 데이터를 User Entity로 변환
     * - 비밀번호는 BCrypt로 암호화 후 저장
     * - 현재 시간을 가입일(createdAt)로 설정
     * - DB에 저장 후 UserResponse DTO로 반환
     */
    public UserResponse registerUser(UserRequest request) {
        User user = new User();
        user.setId(request.getId());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        // 비밀번호 암호화 필수: 평문 저장하면 보안 위험
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(Instant.now());

        // DB에 저장
        User saved = userRepository.save(user);

        // 저장된 정보를 DTO로 반환
        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getPhone(),
                saved.getCreatedAt()
        );
    }

    /**
     * 로그인 처리
     * 1. 필수 파라미터 확인
     * 2. DB에서 userId로 사용자 조회
     * 3. 존재하지 않으면 인증 실패
     * 4. BCrypt로 비밀번호 검증
     * 5. 검증 성공 시 JWT AccessToken + RefreshToken 발급
     */
    public LoginResponse login(LoginRequest request) throws Exception {
        // 1. 필수 입력값 확인
        if (request.getUserId() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("아이디와 비밀번호 모두 작성해주세요.");
        }

        // 2. DB 조회
        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        if (optionalUser.isEmpty()) {
            // 아이디가 존재하지 않으면 인증 실패
            throw new Exception("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        User user = optionalUser.get();

        // 3. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new Exception("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 4. JWT 발급
        String accessToken = generateToken(user.getId(), ACCESS_TOKEN_EXPIRATION);
        String refreshToken = generateToken(user.getId(), REFRESH_TOKEN_EXPIRATION);

        return new LoginResponse(accessToken, refreshToken);
    }

    /**
     * JWT 토큰 생성
     * @param userId 사용자 식별자
     * @param expiration 토큰 만료 시간 (밀리초)
     * @return JWT 문자열
     */
    private String generateToken(String userId, long expiration) {
        return Jwts.builder()
                .setSubject(userId) // 토큰 소유자
                .setIssuedAt(new Date()) // 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 서명 알고리즘 및 비밀키
                .compact();
    }
}

