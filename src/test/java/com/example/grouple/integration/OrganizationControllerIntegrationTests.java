package com.example.grouple.integration;

import com.example.grouple.dto.organization.request.OrgCreateRequest;
import com.example.grouple.dto.organization.request.OrgUpdateRequest;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.User;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrganizationControllerIntegrationTests extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        organizationRepository.deleteAll();
        userRepository.deleteAll();
        owner = saveUser();
    }

    @Test
    void shouldCreateOrganizationThroughApi() throws Exception {
        OrgCreateRequest request = new OrgCreateRequest();
        request.setName("Integration Org");
        request.setDescription("Integration description");
        request.setCategory("CLUB");
        request.setImage_url("logo.png");

        mockMvc.perform(withApiServletPath(post(apiPath("/organizations")))
                        .with(auth(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("Integration Org"))
                .andExpect(jsonPath("$.data.owner_id").value(owner.getId()));

        List<Organization> organizations = organizationRepository.findAll();
        assertThat(organizations).hasSize(1);
        assertThat(organizations.getFirst().getName()).isEqualTo("Integration Org");
    }

    @Test
    void shouldUpdateOrganizationThroughApi() throws Exception {
        Organization existing = saveOrganization("Original Org");

        OrgUpdateRequest request = new OrgUpdateRequest();
        request.setName("Updated Org");
        request.setDescription("Updated description");
        request.setCategory("SOCIETY");
        request.setImage_url("updated.png");

        mockMvc.perform(withApiServletPath(put(apiPath("/organizations/{orgId}"), existing.getId()))
                        .with(auth(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("Updated Org"))
                .andExpect(jsonPath("$.data.category").value("SOCIETY"));

        Organization updated = organizationRepository.findById(existing.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Updated Org");
        assertThat(updated.getDescription()).isEqualTo("Updated description");
        assertThat(updated.getImage()).isEqualTo("updated.png");
    }

    @Test
    void shouldDeleteOrganizationThroughApi() throws Exception {
        Organization existing = saveOrganization("Disposable Org");

        mockMvc.perform(withApiServletPath(delete(apiPath("/organizations/{orgId}"), existing.getId()))
                        .with(auth(owner)))
                .andExpect(status().isNoContent());

        assertThat(organizationRepository.existsById(existing.getId())).isFalse();
    }

    private User saveUser() {
        User user = new User();
        user.setUsername("owner1");
        user.setPassword("password");
        user.setEmail("owner1" + "@example.com");
        user.setPhone("010-0000-0000");
        user.setImage("image.png");
        return userRepository.saveAndFlush(user);
    }

    private Organization saveOrganization(String name) {
        Organization org = new Organization();
        org.setOwner(owner);
        org.setName(name);
        org.setDescription("description");
        org.setCategory("CATEGORY");
        org.setImage("image.png");
        return organizationRepository.saveAndFlush(org);
    }

    private RequestPostProcessor auth(User user) {
        var principal = new AuthPrincipal(user.getId(), user.getUsername());
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return authentication(new UsernamePasswordAuthenticationToken(principal, null, authorities));
    }
}
