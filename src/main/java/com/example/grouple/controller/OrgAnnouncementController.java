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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "06. 조직 공지사항")
@RestController
@RequestMapping("/organizations/{orgId}/anncs")
@PreAuthorize("isAuthenticated()")
public class OrgAnnouncementController {

    private final AnnouncementService announcementService;

    public OrgAnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    // 공지사항 생성 (변경 없음)
    @PostMapping
    public ResponseEntity<AnnouncementCreateResponse> createAnnouncement(
            @PathVariable Integer orgId,
            @Valid @RequestBody AnnouncementCreateRequest request,
            @AuthenticationPrincipal AuthPrincipal userDetails
    ) {
        Integer currentUserId = userDetails.getId();
        AnnouncementCreateResponse response = announcementService.createAnnouncement(orgId, currentUserId, request);
        return ResponseEntity.ok(ApiResponse.success(response).getData());
    }

    // 2. 공지사항 목록 조회 (Pageable 대신 page, size 사용)
    @GetMapping
    @PreAuthorize("@organizationAuthz.canReadOrg(#orgId) || @organizationAuthz.canManageOrg(#orgId)")
    public ResponseEntity<Page<AnnouncementCreateResponse>> getAnnouncements(
            @PathVariable Integer orgId,
            @RequestParam(defaultValue = "0") int page, // 페이지 번호 (0부터 시작)
            @RequestParam(defaultValue = "10") int size // 페이지 크기
    ) {
        // 정렬 조건: 생성일 최신순 (createdAt, DESC)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<AnnouncementCreateResponse> responsePage = announcementService.getAnnouncementsByOrgId(orgId, pageable);
        return ResponseEntity.ok(responsePage);
    }

    // 공지사항 상세 조회 (변경 없음)
    @GetMapping("/{announcementId}")
    @PreAuthorize("@organizationAuthz.canReadOrg(#orgId) || @organizationAuthz.canManageOrg(#orgId)")
    public ResponseEntity<AnnouncementCreateResponse> getAnnouncement(
            @PathVariable Integer orgId,
            @PathVariable Integer announcementId
    ) {
        AnnouncementCreateResponse response = announcementService.getAnnouncementByIdAndOrgId(announcementId, orgId);
        return ResponseEntity.ok(response);
    }

    // 4. 중요 공지사항 목록 조회 (Pageable 대신 page, size 사용)
    @GetMapping("/starred")
    @PreAuthorize("@organizationAuthz.canReadOrg(#orgId) || @organizationAuthz.canManageOrg(#orgId)")
    public ResponseEntity<Page<AnnouncementCreateResponse>> getStarredAnnouncements(
            @PathVariable Integer orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 정렬 조건: 필요시 추가 (여기서는 생성일 역순으로 설정)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<AnnouncementCreateResponse> responsePage = announcementService.getStarredAnnouncements(orgId, pageable);
        return ResponseEntity.ok(responsePage);
    }

    // 5. 공지사항 제목으로 검색 (Pageable 대신 page, size 사용)
    @GetMapping("/search")
    @PreAuthorize("@organizationAuthz.canReadOrg(#orgId) || @organizationAuthz.canManageOrg(#orgId)")
    public ResponseEntity<Page<AnnouncementCreateResponse>> searchAnnouncements(
            @PathVariable Integer orgId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 정렬 조건: 생성일 역순
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<AnnouncementCreateResponse> responsePage = announcementService.searchAnnouncementByTitles(orgId, keyword, pageable);
        return ResponseEntity.ok(responsePage);
    }

    @PatchMapping("/{announcementId}")
    public ResponseEntity<AnnouncementCreateResponse> updateAnnouncement(
            @PathVariable Integer orgId,
            @PathVariable Integer announcementId,
            @Valid @RequestBody AnnouncementCreateRequest request
    ) {
        AnnouncementCreateResponse response = announcementService.updateAnnouncement(announcementId, orgId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{announcementId}/star")
    public ResponseEntity<AnnouncementCreateResponse> toggleStar(
            @PathVariable Integer orgId,
            @PathVariable Integer announcementId
    ) {
        AnnouncementCreateResponse response = announcementService.toggleStar(announcementId, orgId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{announcementId}")
    public ResponseEntity<Void> deleteAnnouncement(
            @PathVariable Integer orgId,
            @PathVariable Integer announcementId
    ) {
        announcementService.deleteAnnouncement(announcementId, orgId);
        return ResponseEntity.noContent().build();
    }
}