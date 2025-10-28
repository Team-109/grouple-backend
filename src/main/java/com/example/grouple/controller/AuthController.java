package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.auth.request.LoginRequest;
import com.example.grouple.dto.auth.request.RegisterRequest;
import com.example.grouple.dto.auth.response.LoginResponse;
import com.example.grouple.dto.auth.response.RegisterResponse;
import com.example.grouple.dto.auth.response.UserInfoResponse;
import com.example.grouple.dto.user.request.UserModifyRequest;
import com.example.grouple.dto.user.response.UserModifyResponse;
import com.example.grouple.service.AuthService;
import com.example.grouple.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "01. Auth", description = "인증 관련 API")
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
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) throws Exception {
        var res = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @GetMapping("/check_id")
    public ResponseEntity<?> checkIdAvailable(@RequestParam String username) throws Exception {
        var found = userService.existsByUsername(username);
        if (found) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken");
        } else {
            return ResponseEntity.ok(ApiResponse.success("Username " + username + " is available"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws Exception {
        var res = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal(expression = "id") Integer meId) throws Exception {
        userService.getUserById(meId);
        return ResponseEntity.ok(ApiResponse.success("Authenticated"));
    }
}

