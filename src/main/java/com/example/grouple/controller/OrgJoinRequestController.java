package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.joinrequest.request.JoinRequestCreateRequest;
import com.example.grouple.dto.joinrequest.request.JoinRequestDecisionRequest;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.JoinRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "05. 조직 가입 요청")
@RestController
@RequestMapping("/organizations/{orgId}/join-requests")
public class OrgJoinRequestController extends BaseController {

    private final JoinRequestService joinRequestService;

    public OrgJoinRequestController(JoinRequestService joinRequestService) {
        this.joinRequestService = joinRequestService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getJoinRequests(@AuthenticationPrincipal AuthPrincipal principal,
                                             @PathVariable Integer orgId) {
        var res = joinRequestService.getOrganizationJoinRequests(requireUserId(principal), orgId);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createJoinRequest(@AuthenticationPrincipal AuthPrincipal principal,
                                               @PathVariable Integer orgId,
                                               @Valid @RequestBody(required = false) JoinRequestCreateRequest request) {
        var res = joinRequestService.createJoinRequestByOrgId(requireUserId(principal), orgId, request);
        return ResponseEntity.status(201).body(ApiResponse.success(res));
    }

    @GetMapping("/{reqId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getJoinRequest(@AuthenticationPrincipal AuthPrincipal principal,
                                            @PathVariable Integer orgId,
                                            @PathVariable Integer reqId) {
        var res = joinRequestService.getOrganizationJoinRequest(requireUserId(principal), orgId, reqId);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @PostMapping("/{reqId}/approve")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> approveJoinRequest(@AuthenticationPrincipal AuthPrincipal principal,
                                                @PathVariable Integer orgId,
                                                @PathVariable Integer reqId,
                                                @Valid @RequestBody(required = false) JoinRequestDecisionRequest request) {
        var res = joinRequestService.approveJoinRequest(requireUserId(principal), orgId, reqId, request);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @PostMapping("/{reqId}/reject")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> rejectJoinRequest(@AuthenticationPrincipal AuthPrincipal principal,
                                               @PathVariable Integer orgId,
                                               @PathVariable Integer reqId,
                                               @Valid @RequestBody(required = false) JoinRequestDecisionRequest request) {
        var res = joinRequestService.rejectJoinRequest(requireUserId(principal), orgId, reqId, request);
        return ResponseEntity.ok(ApiResponse.success(res));
    }
}
