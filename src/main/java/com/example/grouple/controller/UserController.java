package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.user.request.UserDeleteRequest;
import com.example.grouple.dto.user.request.UserImageModifyForm;
import com.example.grouple.dto.user.request.UserModifyRequest;
import com.example.grouple.service.OrganizationService;
import com.example.grouple.service.UserService;
import com.example.grouple.security.AuthPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "02. 유저")
@RestController
@RequestMapping("/users") // api 명세에 따라 변경
public class UserController {

    private final UserService userService;
    private final OrganizationService organizationService;

    public UserController(UserService userService, OrganizationService organizationService) {
        this.userService = userService;
        this.organizationService = organizationService;
    }
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal AuthPrincipal principal) {
        var res = userService.getUserById(requireUserId(principal));
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUserInfo(@AuthenticationPrincipal AuthPrincipal principal, @Valid @RequestBody UserModifyRequest request) throws Exception {
        var updated = userService.update(requireUserId(principal), request);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @PutMapping("/me/image")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUserPhoto(@AuthenticationPrincipal AuthPrincipal principal, @ModelAttribute UserImageModifyForm form) throws Exception {
        var updated = userService.updateImage(requireUserId(principal), form);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal AuthPrincipal principal, @RequestBody UserDeleteRequest request) throws Exception {
        userService.deleteUser(requireUserId(principal), request);
        return ResponseEntity.ok(ApiResponse.success("계정이 정상적으로 삭제되었습니다."));
    }

    @GetMapping("/me/organizations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserOrganizations(@AuthenticationPrincipal AuthPrincipal principal){
        var res = organizationService.getOrgsByOwner_Id(requireUserId(principal));
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    private Integer requireUserId(AuthPrincipal principal) {
        if (principal == null || principal.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보를 확인할 수 없습니다.");
        }
        return principal.getId();
    }
}
