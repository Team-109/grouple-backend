package com.example.grouple.controller;

import com.example.grouple.api.ApiResponse;
import com.example.grouple.dto.announcement.request.AnnouncementCreateRequest;
import com.example.grouple.dto.announcement.response.AnnouncementCreateResponse;
import com.example.grouple.security.AuthPrincipal;
import com.example.grouple.service.AnnouncementService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrgAnnouncementControllerTests {

    @Mock
    private AnnouncementService announcementService;

    private OrgAnnouncementController controller;

    @Captor
    private ArgumentCaptor<PageRequest> pageRequestCaptor;

    @BeforeEach
    void setUp() {
        controller = new OrgAnnouncementController(announcementService);
    }

    @Test
    void createAnnouncement_shouldReturnCreatedResponse() {
        AnnouncementCreateRequest request = new AnnouncementCreateRequest();
        request.setTitle("title");
        request.setDescription("desc");
        AuthPrincipal principal = new AuthPrincipal(5, "user");
        AnnouncementCreateResponse response = AnnouncementCreateResponse.builder()
                .id(1)
                .title("title")
                .description("desc")
                .createdAt(Instant.now())
                .star(false)
                .userId(principal.getId())
                .organizationId(10)
                .build();
        when(announcementService.createAnnouncement(10, principal.getId(), request)).thenReturn(response);

        ResponseEntity<?> result = controller.createAnnouncement(10, request, principal);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getStatus()).isEqualTo("success");
        assertThat(body.getData()).isEqualTo(response);
        verify(announcementService).createAnnouncement(10, principal.getId(), request);
    }

    @Test
    void getAnnouncements_shouldReturnPagedAnnouncements() {
        AnnouncementCreateResponse item = AnnouncementCreateResponse.builder()
                .id(2)
                .title("hello")
                .build();
        Page<AnnouncementCreateResponse> page = new PageImpl<>(List.of(item));
        when(announcementService.getAnnouncementsByOrgId(eq(3), any(PageRequest.class))).thenReturn(page);

        ResponseEntity<?> result = controller.getAnnouncements(3, 0, 20);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        @SuppressWarnings("unchecked")
        Page<AnnouncementCreateResponse> data = (Page<AnnouncementCreateResponse>) body.getData();
        assertThat(data.getContent()).containsExactly(item);
        verify(announcementService).getAnnouncementsByOrgId(eq(3), pageRequestCaptor.capture());
        PageRequest pageable = pageRequestCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(20);
        assertThat(pageable.getSort()).isEqualTo(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Test
    void getAnnouncement_shouldReturnSingleAnnouncement() {
        AnnouncementCreateResponse response = AnnouncementCreateResponse.builder()
                .id(5)
                .title("single")
                .build();
        when(announcementService.getAnnouncementByIdAndOrgId(5, 1)).thenReturn(response);

        ResponseEntity<?> result = controller.getAnnouncement(1, 5);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getData()).isEqualTo(response);
        verify(announcementService).getAnnouncementByIdAndOrgId(5, 1);
    }

    @Test
    void getStarredAnnouncements_shouldReturnStarredPage() {
        AnnouncementCreateResponse item = AnnouncementCreateResponse.builder()
                .id(7)
                .title("starred")
                .build();
        Page<AnnouncementCreateResponse> page = new PageImpl<>(List.of(item));
        when(announcementService.getStarredAnnouncements(eq(4), any(PageRequest.class))).thenReturn(page);

        ResponseEntity<?> result = controller.getStarredAnnouncements(4, 1, 5);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        @SuppressWarnings("unchecked")
        Page<AnnouncementCreateResponse> data = (Page<AnnouncementCreateResponse>) body.getData();
        assertThat(data.getContent()).containsExactly(item);
        verify(announcementService).getStarredAnnouncements(eq(4), pageRequestCaptor.capture());
        PageRequest pageable = pageRequestCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(5);
    }

    @Test
    void searchAnnouncements_shouldReturnSearchResults() {
        AnnouncementCreateResponse item = AnnouncementCreateResponse.builder()
                .id(8)
                .title("keyword hit")
                .build();
        Page<AnnouncementCreateResponse> page = new PageImpl<>(List.of(item));
        when(announcementService.searchAnnouncementByTitles(eq(6), eq("key"), any(PageRequest.class)))
                .thenReturn(page);

        ResponseEntity<?> result = controller.searchAnnouncements(6, "key", 0, 10);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        @SuppressWarnings("unchecked")
        Page<AnnouncementCreateResponse> data = (Page<AnnouncementCreateResponse>) body.getData();
        assertThat(data.getContent()).containsExactly(item);
        verify(announcementService).searchAnnouncementByTitles(eq(6), eq("key"), pageRequestCaptor.capture());
        assertThat(pageRequestCaptor.getValue().getPageSize()).isEqualTo(10);
    }

    @Test
    void updateAnnouncement_shouldReturnUpdatedData() {
        AnnouncementCreateRequest request = new AnnouncementCreateRequest();
        request.setTitle("updated");
        AnnouncementCreateResponse response = AnnouncementCreateResponse.builder()
                .id(3)
                .title("updated")
                .build();
        when(announcementService.updateAnnouncement(3, 2, request)).thenReturn(response);

        ResponseEntity<?> result = controller.updateAnnouncement(2, 3, request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getData()).isEqualTo(response);
        verify(announcementService).updateAnnouncement(3, 2, request);
    }

    @Test
    void toggleStar_shouldReturnToggledData() {
        AnnouncementCreateResponse response = AnnouncementCreateResponse.builder()
                .id(4)
                .star(true)
                .build();
        when(announcementService.toggleStar(4, 9)).thenReturn(response);

        ResponseEntity<?> result = controller.toggleStar(9, 4);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        ApiResponse<?> body = (ApiResponse<?>) result.getBody();
        assertThat(body.getData()).isEqualTo(response);
        verify(announcementService).toggleStar(4, 9);
    }

    @Test
    void deleteAnnouncement_shouldReturnNoContent() {
        ResponseEntity<?> result = controller.deleteAnnouncement(1, 2);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(announcementService).deleteAnnouncement(2, 1);
    }
}
