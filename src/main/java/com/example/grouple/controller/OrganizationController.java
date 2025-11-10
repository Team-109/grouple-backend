package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.organization.request.OrgCreateRequest;
import com.example.grouple.dto.organization.request.OrgUpdateRequest;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.OrganizationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "03. 조직")
@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    private final OrganizationService orgService;

    public OrganizationController(OrganizationService orgService) {
        this.orgService = orgService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createOrg(@AuthenticationPrincipal AuthPrincipal principal, @Valid @RequestBody OrgCreateRequest req) {
        var res = orgService.createOrg(requireUserId(principal), req);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getOrgList() {
        var res = orgService.getAllOrgs();
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @GetMapping("/{orgId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getOrgDetail(@PathVariable Integer orgId) {
        var res = orgService.getOrgById(orgId);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @PutMapping("/{orgId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateOrg(@AuthenticationPrincipal AuthPrincipal principal,
                                       @PathVariable Integer orgId,
                                       @Valid @RequestBody OrgUpdateRequest request) {
        var res = orgService.updateOrg(requireUserId(principal), orgId, request);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    @DeleteMapping("/{orgId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteOrg(@AuthenticationPrincipal AuthPrincipal principal,
                                       @PathVariable Integer orgId) {
        var res = orgService.deleteOrg(requireUserId(principal), orgId);
        return ResponseEntity.ok(ApiResponse.success(res));
    }

    private Integer requireUserId(AuthPrincipal principal) {
        if (principal == null || principal.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "인증 정보를 확인할 수 없습니다.");
        }
        return principal.getId();
    }
}
