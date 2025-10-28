package com.example.grouple.service;

import com.example.grouple.dto.user.request.UserDeleteRequest;
import com.example.grouple.dto.user.request.UserImageModifyForm;
import com.example.grouple.dto.user.request.UserModifyRequest;
import com.example.grouple.dto.auth.request.RegisterRequest;
import com.example.grouple.dto.auth.response.UserInfoResponse;
import com.example.grouple.dto.user.response.UserImageModifyResponse;
import com.example.grouple.dto.user.response.UserModifyResponse;
import com.example.grouple.dto.auth.response.RegisterResponse;
import com.example.grouple.entity.User;
import com.example.grouple.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final SecretKey jwtKey;

    /**
     * 아이디 중복 체크
     * 회원가입 시 동일 ID 존재 여부 확인
     */
    public boolean existsByUsername(String username) {
        return repo.existsByUsername(username);
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserById(Integer id) {
        User user = repo.findById(id).orElseThrow();
        return new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone(),
                user.getImage(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    /**
     * 회원가입 처리
     * - UserRequest에서 전달받은 데이터를 User Entity로 변환
     * - 비밀번호는 BCrypt로 암호화 후 저장
     * - 현재 시간을 가입일(createdAt)로 설정
     * - DB에 저장 후 UserResponse DTO로 반환
     */
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // 필수 입력값 확인
        if (request.getUsername() == null || request.getUsername().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty() ||
                request.getPasswordConfirm() == null || request.getPasswordConfirm().isEmpty() ||
                request.getEmail() == null || request.getEmail().isEmpty() ||
                request.getPhone() == null || request.getPhone().isEmpty()) {

            throw new IllegalArgumentException("필수 입력 값이 누락되었습니다.");
        }

        // 비밀번호 확인
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        if (existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("중복된 아이디 입니다.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setCreatedAt(Instant.now());
        User saved = repo.save(user);
        repo.flush();
        return new RegisterResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getPhone(),
                saved.getImage(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    @PreAuthorize("@userAuthz.canEditUser(#id)")
    @Transactional
    public UserModifyResponse update(@P("id") Integer id, UserModifyRequest req) throws Exception {
        User user = repo.findById(id)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));
        if (req.getUsername() != null)
            user.setUsername(req.getUsername());
        if (req.getEmail() != null)
            user.setEmail(req.getEmail());
        if (req.getPhone() != null)
            user.setPhone(req.getPhone());
        if (req.getPassword() != null && !req.getPassword().isBlank())
            user.setPassword(encoder.encode(req.getPassword()));
        User saved = repo.save(user);
        repo.flush();
        return new UserModifyResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getPhone(),
                saved.getImage(),
                saved.getCreatedAt(),
                saved.getUpdatedAt()
        );
    }

    @PreAuthorize("@userAuthz.canEditUser(#id)")
    @Transactional
    public UserImageModifyResponse updateImage(@P("id") Integer id, UserImageModifyForm form) throws Exception {
        User user = repo.findById(id)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));
        user.setImage(form.getImage());
        User saved = repo.save(user);
        repo.flush();
        return new UserImageModifyResponse(
                saved.getImage(),
                saved.getUpdatedAt()
        );
    }
    @PreAuthorize("@userAuthz.canEditUser(#id)")
    @Transactional
    public void deleteUser(@P("id") Integer id, UserDeleteRequest request) throws Exception {
        User user = repo.findById(id)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));
        repo.delete(user);
        repo.flush();
    }
}

