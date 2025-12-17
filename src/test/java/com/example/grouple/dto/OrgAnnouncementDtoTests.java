package com.example.grouple.dto;

import com.example.grouple.dto.announcement.request.AnnouncementCreateRequest;
import com.example.grouple.dto.announcement.response.AnnouncementCreateResponse;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class OrgAnnouncementDtoTests {

    @Test
    void announcementCreateRequest_shouldHoldValues() {
        AnnouncementCreateRequest req = new AnnouncementCreateRequest();
        req.setTitle("title");
        req.setDescription("desc");
        assertThat(req.getTitle()).isEqualTo("title");
        assertThat(req.getDescription()).isEqualTo("desc");
    }

    @Test
    void announcementCreateResponse_shouldExposeFields() {
        Instant now = Instant.now();
        AnnouncementCreateResponse res = AnnouncementCreateResponse.builder()
                .id(1)
                .title("title")
                .description("desc")
                .createdAt(now)
                .star(true)
                .userId(2)
                .organizationId(3)
                .build();

        assertThat(res.getId()).isEqualTo(1);
        assertThat(res.getTitle()).isEqualTo("title");
        assertThat(res.getStar()).isTrue();
        assertThat(res.getOrganizationId()).isEqualTo(3);
    }
}
