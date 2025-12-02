package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.common.ConflictException;
import com.example.grouple.common.UnauthorizedException;
import com.example.grouple.dto.auth.request.LoginRequest;
import com.example.grouple.dto.auth.request.RegisterRequest;
import com.example.grouple.dto.auth.request.RefreshTokenRequest;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.AuthService;
import com.example.grouple.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "01. 인증")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        var res = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @GetMapping("/check-id")
    public ResponseEntity<?> checkIdAvailable(@RequestParam String username) {
        var found = userService.existsByUsername(username);
        if (found) {
            throw new ConflictException("Username is already taken");
        } else {
            return ResponseEntity.ok(ApiResponse.success("Username " + username + " is available"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws Exception {
        var res = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request) throws Exception {
        var res = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal AuthPrincipal principal) {
        var res = userService.getUserById(requireUserId(principal));
        return ResponseEntity.ok(ApiResponse.success("로그인된 사용자 아이디: " + res.getUsername()));
    }

    private Integer requireUserId(AuthPrincipal principal) {
        if (principal == null || principal.getId() == null) {
            throw new UnauthorizedException("인증 정보를 확인할 수 없습니다.");
        }
        return principal.getId();
    }
}
