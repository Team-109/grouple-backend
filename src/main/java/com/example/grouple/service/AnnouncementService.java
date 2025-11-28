package com.example.grouple.service;

import com.example.grouple.dto.announcement.request.AnnouncementCreateRequest;
import com.example.grouple.dto.announcement.response.AnnouncementCreateResponse;
import com.example.grouple.entity.Announcement;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.User;
import com.example.grouple.repository.AnnouncementRepository;
import com.example.grouple.repository.OrganizationRepository;
import com.example.grouple.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcementRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    // 공지사항 생성
    @Transactional
    public AnnouncementCreateResponse createAnnouncement(Integer organizationId, Integer userId, AnnouncementCreateRequest request) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new EntityNotFoundException("Organization not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setDescription(request.getDescription());
        announcement.setOrganization(organization);
        announcement.setUser(user);

        Announcement savedAnnouncement = announcementRepository.save(announcement);

        return toResponse(savedAnnouncement);
    }

    // 공지사항 목록 가져오기
    @Transactional(readOnly = true)
    public Page<AnnouncementCreateResponse> getAnnouncementsByOrgId(Integer organizationId, Pageable pageable) {
        Page<Announcement> announcementPage = announcementRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId, pageable);

        return announcementPage.map(this::toResponse);
    }

    // 공지사항 1개 가져오기
    @Transactional(readOnly = true)
    public AnnouncementCreateResponse getAnnouncementByIdAndOrgId(Integer id, Integer organizationId) {
        Announcement announcement = announcementRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + id));

        // 엔티티를 DTO로 변환하여 반환
        return toResponse(announcement);
    }

    // 중요 공지사항 목록 가져오기
    @Transactional(readOnly = true)
    public Page<AnnouncementCreateResponse> getStarredAnnouncements(Integer organizationId, Pageable pageable) {
        Page<Announcement> announcementPage = announcementRepository.findByOrganizationIdAndStar(organizationId, Boolean.TRUE, pageable);

        // Page<Entity> -> Page<DTO>로 변환하여 반환
        return announcementPage.map(this::toResponse);
    }

    // 공지사항 제목으로 검색
    @Transactional(readOnly = true)
    public Page<AnnouncementCreateResponse> searchAnnouncementByTitles(Integer organizationId, String keyword, Pageable pageable) {
        Page<Announcement> announcementPage = announcementRepository.findByOrganizationIdAndTitleContaining(organizationId, keyword, pageable);

        // Page<Entity> -> Page<DTO>로 변환하여 반환
        return announcementPage.map(this::toResponse);
    }

    @Transactional
    public AnnouncementCreateResponse updateAnnouncement(Integer id, Integer organizationId, AnnouncementCreateRequest req) {
        Announcement oldAnnouncement = announcementRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + id));

        if (req.getTitle() != null)
            oldAnnouncement.setTitle(req.getTitle());
        if (req.getDescription() != null)
            oldAnnouncement.setDescription(req.getDescription());

        Announcement saved = announcementRepository.save(oldAnnouncement);

        // 엔티티를 DTO로 변환하여 반환
        return toResponse(saved);
    }

    // 공지사항 star 토글
    @Transactional
    public AnnouncementCreateResponse toggleStar(Integer id, Integer organizationId){
        Announcement announcement = announcementRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + id));

        Boolean currentStar = announcement.getStar();
        announcement.setStar(!currentStar);

        Announcement saved = announcementRepository.save(announcement);

        // 엔티티를 DTO로 변환하여 반환
        return toResponse(saved);
    }

    //공지사항 삭제
    @Transactional
    public void deleteAnnouncement(Integer id, Integer organizationId) {
        Announcement announcement = announcementRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new EntityNotFoundException("공지사항을 찾을 수 없습니다. ID: " + id));

        announcementRepository.delete(announcement);
    }

    private AnnouncementCreateResponse toResponse(Announcement announcement) {
        return AnnouncementCreateResponse.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .description(announcement.getDescription())
                .createdAt(announcement.getCreatedAt())
                .star(announcement.getStar())
                .userId(announcement.getUser().getId())
                .organizationId(announcement.getOrganization().getId())
                .build();
    }
}

