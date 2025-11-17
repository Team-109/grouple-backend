package com.example.grouple.controller;

import com.example.grouple.dto.announcement.request.AnnouncementCreateRequest;
import com.example.grouple.dto.announcement.response.AnnouncementCreateResponse;
import com.example.grouple.security.CustomUserDetails;
import com.example.grouple.service.AnnouncementService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "06. 조직 공지사항")
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/organizations/{orgId}/anncs")
public class OrgAnnouncementController {

    private final AnnouncementService announcementService;

    public OrgAnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    // 공지사항 생성
    @PostMapping
    public ResponseEntity<AnnouncementCreateResponse> createAnnouncement(
            @PathVariable Integer organizationId,
            @Valid @RequestBody AnnouncementCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails // <-- 1. CustomUserDetails 객체 전체를 받습니다.
    ) {
        Integer currentUserId = userDetails.getId(); // (또는 .getUserId() 등 CustomUserDetails에 정의된 메소드)

        AnnouncementCreateResponse response = announcementService.createAnnouncement(organizationId, currentUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 공지사항 목록 조회 (페이징)
    @GetMapping
    @PreAuthorize("@organizationAuthz.canReadOrg(#organizationId)")
    public ResponseEntity<Page<AnnouncementCreateResponse>> getAnnouncements(
            @PathVariable Integer organizationId,
            @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable
    ) {
        Page<AnnouncementCreateResponse> responsePage = announcementService.getAnnouncementsByOrgId(organizationId, pageable);
        return ResponseEntity.ok(responsePage);
    }

    //공지사항 상세 조회
    @GetMapping("/{announcementId}")
    @PreAuthorize("@organizationAuthz.canReadOrg(#organizationId)")
    public ResponseEntity<AnnouncementCreateResponse> getAnnouncement(
            @PathVariable Integer organizationId,
            @PathVariable Integer announcementId
    ) {
        AnnouncementCreateResponse response = announcementService.getAnnouncementByIdAndOrgId(announcementId, organizationId);
        return ResponseEntity.ok(response);
    }

    //4. 중요 공지사항 목록 조회
    @GetMapping("/starred")
    @PreAuthorize("@organizationAuthz.canReadOrg(#organizationId)") // <-- 2️⃣단계: 조직 멤버(Reader)인가?
    public ResponseEntity<Page<AnnouncementCreateResponse>> getStarredAnnouncements(
            @PathVariable Integer organizationId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<AnnouncementCreateResponse> responsePage = announcementService.getStarredAnnouncements(organizationId, pageable);
        return ResponseEntity.ok(responsePage);
    }

    // 5. 공지사항 제목으로 검색
    @GetMapping("/search")
    @PreAuthorize("@organizationAuthz.canReadOrg(#organizationId)") // <-- 2️⃣단계: 조직 멤버(Reader)인가?
    public ResponseEntity<Page<AnnouncementCreateResponse>> searchAnnouncements(
            @PathVariable Integer organizationId,
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<AnnouncementCreateResponse> responsePage = announcementService.searchAnnouncementByTitles(organizationId, keyword, pageable);
        return ResponseEntity.ok(responsePage);
    }

    //공지사항 수정 (제목, 내용)
    @PatchMapping("/{announcementId}")
    @PreAuthorize("@organizationAuthz.canManageOrg(#organizationId)") // <-- 2️⃣단계: 조직 관리자(Owner)인가?
    public ResponseEntity<AnnouncementCreateResponse> updateAnnouncement(
            @PathVariable Integer organizationId,
            @PathVariable Integer announcementId,
            @Valid @RequestBody AnnouncementCreateRequest request // [스타일 적용] @Valid
    ) {
        AnnouncementCreateResponse response = announcementService.updateAnnouncement(announcementId, organizationId, request);
        return ResponseEntity.ok(response);
    }

    //공지사항 Star 토글
     @PatchMapping("/{announcementId}/star")
    @PreAuthorize("@organizationAuthz.canManageOrg(#organizationId)") // <-- 2️⃣단계: 조직 관리자(Owner)인가?
    public ResponseEntity<AnnouncementCreateResponse> toggleStar(
            @PathVariable Integer organizationId,
            @PathVariable Integer announcementId
    ) {
        AnnouncementCreateResponse response = announcementService.toggleStar(announcementId, organizationId);
        return ResponseEntity.ok(response);
    }

    //공지사항 삭제
     @DeleteMapping("/{announcementId}")
    @PreAuthorize("@organizationAuthz.canManageOrg(#organizationId)") // <-- 2️⃣단계: 조직 관리자(Owner)인가?
    public ResponseEntity<Void> deleteAnnouncement(
            @PathVariable Integer organizationId,
            @PathVariable Integer announcementId
    ) {
        announcementService.deleteAnnouncement(announcementId, organizationId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
