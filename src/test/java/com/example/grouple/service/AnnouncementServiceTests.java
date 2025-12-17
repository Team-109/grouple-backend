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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceTests {

    @Mock
    private AnnouncementRepository announcementRepository;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private UserRepository userRepository;

    private AnnouncementService announcementService;

    private Organization org;
    private User user;

    @Captor
    private ArgumentCaptor<Announcement> announcementCaptor;

    @BeforeEach
    void setUp() {
        announcementService = new AnnouncementService(announcementRepository, organizationRepository, userRepository);
        org = new Organization();
        org.setId(1);
        user = new User();
        user.setId(2);
        user.setUsername("user");
    }

    @Test
    void createAnnouncement_shouldSaveAndReturnResponse() {
        AnnouncementCreateRequest request = new AnnouncementCreateRequest();
        request.setTitle("title");
        request.setDescription("desc");
        when(organizationRepository.findById(1)).thenReturn(Optional.of(org));
        when(userRepository.findById(2)).thenReturn(Optional.of(user));
        when(announcementRepository.save(any(Announcement.class))).thenAnswer(invocation -> {
            Announcement a = invocation.getArgument(0);
            a.setId(10);
            a.setCreatedAt(Instant.EPOCH);
            return a;
        });

        AnnouncementCreateResponse response = announcementService.createAnnouncement(1, 2, request);

        assertThat(response.getId()).isEqualTo(10);
        assertThat(response.getTitle()).isEqualTo("title");
        assertThat(response.getUserId()).isEqualTo(2);
        assertThat(response.getOrganizationId()).isEqualTo(1);
        verify(announcementRepository).save(announcementCaptor.capture());
        assertThat(announcementCaptor.getValue().getOrganization()).isSameAs(org);
    }

    @Test
    void getAnnouncementsByOrgId_shouldMapPage() {
        Announcement ann = new Announcement();
        ann.setId(3);
        ann.setTitle("hello");
        ann.setOrganization(org);
        ann.setUser(user);
        Page<Announcement> page = new PageImpl<>(List.of(ann));
        when(announcementRepository.findByOrganizationIdOrderByCreatedAtDesc(eq(1), any(PageRequest.class))).thenReturn(page);

        Page<AnnouncementCreateResponse> result = announcementService.getAnnouncementsByOrgId(1, PageRequest.of(0, 10, Sort.by("createdAt")));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(3);
    }

    @Test
    void getAnnouncementByIdAndOrgId_shouldThrowWhenMissing() {
        when(announcementRepository.findByIdAndOrganizationId(5, 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> announcementService.getAnnouncementByIdAndOrgId(5, 1))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void toggleStar_shouldFlipBoolean() {
        Announcement ann = new Announcement();
        ann.setId(7);
        ann.setTitle("toggle");
        ann.setStar(false);
        ann.setOrganization(org);
        ann.setUser(user);
        when(announcementRepository.findByIdAndOrganizationId(7, 1)).thenReturn(Optional.of(ann));
        when(announcementRepository.save(any(Announcement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnnouncementCreateResponse response = announcementService.toggleStar(7, 1);

        assertThat(response.getStar()).isTrue();
        verify(announcementRepository).save(ann);
    }

    @Test
    void updateAnnouncement_shouldApplyChanges() {
        AnnouncementCreateRequest request = new AnnouncementCreateRequest();
        request.setTitle("new title");
        request.setDescription("new desc");
        Announcement ann = new Announcement();
        ann.setId(12);
        ann.setTitle("old");
        ann.setDescription("old");
        ann.setOrganization(org);
        ann.setUser(user);
        when(announcementRepository.findByIdAndOrganizationId(12, 1)).thenReturn(Optional.of(ann));
        when(announcementRepository.save(any(Announcement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AnnouncementCreateResponse response = announcementService.updateAnnouncement(12, 1, request);

        assertThat(response.getTitle()).isEqualTo("new title");
        assertThat(response.getDescription()).isEqualTo("new desc");
        verify(announcementRepository).save(ann);
    }

    @Test
    void deleteAnnouncement_shouldInvokeRepositoryDelete() {
        Announcement ann = new Announcement();
        ann.setId(9);
        ann.setOrganization(org);
        ann.setUser(user);
        when(announcementRepository.findByIdAndOrganizationId(9, 1)).thenReturn(Optional.of(ann));

        announcementService.deleteAnnouncement(9, 1);

        verify(announcementRepository).delete(ann);
    }
}
