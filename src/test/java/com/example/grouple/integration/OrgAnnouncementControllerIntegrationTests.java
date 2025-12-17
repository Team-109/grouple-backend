package com.example.grouple.integration;

import com.example.grouple.dto.announcement.request.AnnouncementCreateRequest;
import com.example.grouple.entity.Announcement;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.User;
import com.example.grouple.repository.AnnouncementRepository;
import com.example.grouple.repository.OrganizationRepository;
import com.example.grouple.repository.UserRepository;
import com.example.grouple.security.AuthPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrgAnnouncementControllerIntegrationTests extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private AnnouncementRepository announcementRepository;

    private User user;
    private Organization org;

    @BeforeEach
    void setup() {
        announcementRepository.deleteAll();
        organizationRepository.deleteAll();
        userRepository.deleteAll();
        user = saveUser("ann");
        org = saveOrganization(user, "Org");
    }

    @Test
    void create_list_toggle_delete_announcement() throws Exception {
        AnnouncementCreateRequest request = new AnnouncementCreateRequest();
        request.setTitle("title");
        request.setDescription("desc");

        mockMvc.perform(withApiServletPath(post(apiPath("/organizations/{orgId}/announcements"), org.getId()))
                        .with(auth(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("title"));

        // list
        mockMvc.perform(withApiServletPath(get(apiPath("/organizations/{orgId}/announcements"), org.getId()))
                        .with(auth(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1));

        Announcement ann = announcementRepository.findAll().getFirst();

        // toggle star
        mockMvc.perform(withApiServletPath(patch(apiPath("/organizations/{orgId}/announcements/{announcementId}/star"), org.getId(), ann.getId()))
                        .with(auth(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.star").value(true));

        // delete
        mockMvc.perform(withApiServletPath(delete(apiPath("/organizations/{orgId}/announcements/{announcementId}"), org.getId(), ann.getId()))
                        .with(auth(user)))
                .andExpect(status().isNoContent());

        assertThat(announcementRepository.existsById(ann.getId())).isFalse();
    }

    @Test
    void search_and_get_starred() throws Exception {
        Announcement a1 = announcementRepository.save(buildAnnouncement("hello", false));
        Announcement a2 = announcementRepository.save(buildAnnouncement("keyword hit", true));

        mockMvc.perform(withApiServletPath(get(apiPath("/organizations/{orgId}/announcements/search"), org.getId()))
                        .with(auth(user))
                        .param("keyword", "keyword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(a2.getId()));

        mockMvc.perform(withApiServletPath(get(apiPath("/organizations/{orgId}/announcements/starred"), org.getId()))
                        .with(auth(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    private Announcement buildAnnouncement(String title, boolean star) {
        Announcement ann = new Announcement();
        ann.setTitle(title);
        ann.setDescription("desc");
        ann.setOrganization(org);
        ann.setUser(user);
        ann.setStar(star);
        ann.setCreatedAt(Instant.now());
        ann.setUpdatedAt(Instant.now());
        return ann;
    }

    private User saveUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("pw");
        user.setEmail(username + "@example.com");
        user.setPhone("010-0000-0000");
        return userRepository.saveAndFlush(user);
    }

    private Organization saveOrganization(User owner, String name) {
        Organization org = new Organization();
        org.setOwner(owner);
        org.setName(name);
        org.setDescription("desc");
        org.setCategory("CLUB");
        org.setImage("img");
        return organizationRepository.saveAndFlush(org);
    }

    private RequestPostProcessor auth(User user) {
        var principal = new AuthPrincipal(user.getId(), user.getUsername());
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return authentication(new UsernamePasswordAuthenticationToken(principal, null, authorities));
    }
}
