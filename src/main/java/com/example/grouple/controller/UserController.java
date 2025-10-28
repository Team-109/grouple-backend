package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.user.request.UserDeleteRequest;
import com.example.grouple.dto.user.request.UserImageModifyForm;
import com.example.grouple.dto.user.request.UserModifyRequest;
import com.example.grouple.dto.auth.request.RegisterRequest;
import com.example.grouple.dto.auth.response.UserInfoResponse;
import com.example.grouple.dto.user.response.UserImageModifyResponse;
import com.example.grouple.dto.user.response.UserModifyResponse;
import com.example.grouple.dto.auth.response.RegisterResponse;
import com.example.grouple.dto.auth.request.LoginRequest;
import com.example.grouple.dto.auth.response.LoginResponse;
import com.example.grouple.service.AuthService;
import com.example.grouple.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "02. User", description = "유저 관련 API")
@RestController
@RequestMapping("/users") // api 명세에 따라 변경
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal(expression = "id") Integer meId) throws Exception {
        var res = userService.getUserById(meId);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUserInfo(@AuthenticationPrincipal(expression = "id") Integer meId, @Valid @RequestBody UserModifyRequest request) throws Exception {
        var updated = userService.update(meId, request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @PutMapping("/me/image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUserPhoto(@AuthenticationPrincipal(expression = "id") Integer meId, @ModelAttribute UserImageModifyForm form) throws Exception {
        var updated = userService.updateImage(meId, form);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal(expression = "id") Integer meId, @RequestBody UserDeleteRequest request) throws Exception {
        userService.deleteUser(meId, request);
        return ResponseEntity.ok(ApiResponse.success("계정이 정상적으로 삭제되었습니다."));
    }

}

