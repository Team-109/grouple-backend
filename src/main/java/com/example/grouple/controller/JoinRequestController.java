package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.joinrequest.request.JoinRequestCreateRequest;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.JoinRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "05. 조직 가입 요청")
@RestController
@RequestMapping("/join-requests")
public class JoinRequestController {

    private final JoinRequestService joinRequestService;

    public JoinRequestController(JoinRequestService joinRequestService) {
        this.joinRequestService = joinRequestService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createJoinRequest(@AuthenticationPrincipal AuthPrincipal principal,
                                               @RequestParam("org_code") String orgCode,
                                               @Valid @RequestBody(required = false) JoinRequestCreateRequest request) {
        var res = joinRequestService.createJoinRequest(requireUserId(principal), orgCode, request);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    private Integer requireUserId(AuthPrincipal principal) {
        if (principal == null || principal.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보를 확인할 수 없습니다.");
        }
        return principal.getId();
    }
}
