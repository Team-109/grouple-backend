package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.announcement.request.AnnouncementCreateRequest;
import com.example.grouple.dto.announcement.response.AnnouncementCreateResponse;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.AnnouncementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "06. 조직 공지사항")
@RestController
@RequestMapping("/organizations/{orgId}/announcements")
public class OrgAnnouncementController extends BaseController {

    private final AnnouncementService announcementService;

    public OrgAnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createAnnouncement(
            @PathVariable Integer orgId,
            @Valid @RequestBody AnnouncementCreateRequest request,
            @AuthenticationPrincipal AuthPrincipal principal
    ) {
        Integer currentUserId = requireUserId(principal);
        AnnouncementCreateResponse response = announcementService.createAnnouncement(orgId, currentUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("@organizationAuthz.canReadOrg(#orgId) || @organizationAuthz.canManageOrg(#orgId)")
    public ResponseEntity<?> getAnnouncements(
            @PathVariable Integer orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AnnouncementCreateResponse> responsePage = announcementService.getAnnouncementsByOrgId(orgId, pageable);
        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }

    @GetMapping("/{announcementId}")
    @PreAuthorize("@organizationAuthz.canReadOrg(#orgId) || @organizationAuthz.canManageOrg(#orgId)")
    public ResponseEntity<?> getAnnouncement(
            @PathVariable Integer orgId,
            @PathVariable Integer announcementId
    ) {
        AnnouncementCreateResponse response = announcementService.getAnnouncementByIdAndOrgId(announcementId, orgId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/starred")
    @PreAuthorize("@organizationAuthz.canReadOrg(#orgId) || @organizationAuthz.canManageOrg(#orgId)")
    public ResponseEntity<?> getStarredAnnouncements(
            @PathVariable Integer orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AnnouncementCreateResponse> responsePage = announcementService.getStarredAnnouncements(orgId, pageable);
        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }

    @GetMapping("/search")
    @PreAuthorize("@organizationAuthz.canReadOrg(#orgId) || @organizationAuthz.canManageOrg(#orgId)")
    public ResponseEntity<?> searchAnnouncements(
            @PathVariable Integer orgId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AnnouncementCreateResponse> responsePage = announcementService.searchAnnouncementByTitles(orgId, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }

    @PatchMapping("/{announcementId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateAnnouncement(
            @PathVariable Integer orgId,
            @PathVariable Integer announcementId,
            @Valid @RequestBody AnnouncementCreateRequest request
    ) {
        AnnouncementCreateResponse response = announcementService.updateAnnouncement(announcementId, orgId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{announcementId}/star")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> toggleStar(
            @PathVariable Integer orgId,
            @PathVariable Integer announcementId
    ) {
        AnnouncementCreateResponse response = announcementService.toggleStar(announcementId, orgId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{announcementId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteAnnouncement(
            @PathVariable Integer orgId,
            @PathVariable Integer announcementId
    ) {
        announcementService.deleteAnnouncement(announcementId, orgId);
        return ResponseEntity.noContent().build();
    }
}