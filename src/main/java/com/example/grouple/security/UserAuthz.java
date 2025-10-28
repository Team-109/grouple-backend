package com.example.grouple.security;

import com.example.grouple.repository.OrganizationRepository;
import com.example.grouple.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthz {
    private final UserRepository userRepo;
    private final UserRepository userRepository;

    public boolean canEditUser(Integer userId) {
        return userId.equals((authId())); // 자기 자신만 수정 가능
    }

    public boolean canViewUser(String userId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails user) {
            user.getId();
            return auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

    private Integer authId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Unauthenticated");
        }
        Object p = auth.getPrincipal();

        if (p instanceof com.example.grouple.config.SecurityConfig.AuthPrincipal ap) {
            return ap.id();
        }
        if (p instanceof CustomUserDetails cud) {
            return cud.getId();
        }
        if (p instanceof String s) {
            if ("anonymousUser".equals(s)) throw new IllegalStateException("Unauthenticated");
            // 필요 시 username → id 조회
            return userRepository.findIdByUsername(s)
                    .orElseThrow(() -> new IllegalStateException("User not found: " + s));
        }
        throw new IllegalStateException("Unexpected principal type: " + p.getClass());
    }
}