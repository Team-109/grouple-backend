package com.example.grouple.repository;

import com.example.grouple.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username); // 아이디 중복 체크
    Optional<User> findByUsername(String username);

    Optional<Integer> findIdByUsername(String username);
}