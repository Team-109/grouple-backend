package com.example.grouple.security;


import com.example.grouple.entity.User;
import com.example.grouple.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        List<GrantedAuthority> auths = List.of(() -> "ROLE_USER"); // 필요 시 DB 매핑
        return new CustomUserDetails(u, auths);
    }
}