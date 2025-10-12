package com.example.grouple.repository;

import com.example.grouple.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsById(String id); // 아이디 중복 체크
}