package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.organization.request.OrgMembersDeleteRequest;
import com.example.grouple.dto.organization.request.OrgMembersRequest;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "04. 조직 회원")
@RestController
@RequestMapping("/organizations/{orgId}/members")
public class OrgMemberController {

    private final MemberService memberService;

    public OrgMemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMembers(@PathVariable Integer orgId,
                                        @Valid @ModelAttribute OrgMembersRequest request) {
        var res = memberService.getOrgMembers(orgId, request);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @DeleteMapping("/{memberId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteMember(@AuthenticationPrincipal AuthPrincipal principal,
                                          @PathVariable Integer orgId,
                                          @PathVariable Integer memberId,
                                          @Valid @RequestBody OrgMembersDeleteRequest request) {
        var res = memberService.deleteMember(requireUserId(principal), orgId, memberId, request);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    private Integer requireUserId(AuthPrincipal principal) {
        if (principal == null || principal.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보를 확인할 수 없습니다.");
        }
        return principal.getId();
    }
}
