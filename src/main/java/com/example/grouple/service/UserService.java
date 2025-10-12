package com.example.grouple.service;

import com.example.grouple.dto.UserRequest;
import com.example.grouple.dto.UserResponse;
import com.example.grouple.entity.User;
import com.example.grouple.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository repo) {
        this.userRepository = repo;
    }

    // 아이디 중복 체크
    public boolean existsById(String Id) {
        return userRepository.existsById(Id);
    }

    // 회원가입 처리
    public UserResponse registerUser(UserRequest request) {
        User user = new User();
        user.setId(request.getId());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword()); // 실제 서비스에서는 암호화 필요
        user.setCreatedAt(Instant.now()); // 현재 시간으로 회원가입 시간 설정

        User saved = userRepository.save(user); // DB에 저장

        // 저장된 정보를 DTO로 변환하여 반환
        return new UserResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getPhone(),
                saved.getCreatedAt()
        );
    }
}
