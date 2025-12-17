package com.example.grouple.integration;

import com.example.grouple.dto.auth.request.RegisterRequest;
import com.example.grouple.dto.organization.request.OrgCreateRequest;
import com.example.grouple.dto.user.request.UserDeleteRequest;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.User;
import com.example.grouple.repository.OrganizationRepository;
import com.example.grouple.repository.UserRepository;
import com.example.grouple.security.AuthPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HTTP 상태 코드 통합 테스트
 * 실제 HTTP 요청을 통해 모든 엔드포인트가 올바른 상태 코드를 반환하는지 검증
 */
class HttpStatusCodeIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        organizationRepository.deleteAll();
        userRepository.deleteAll();
        testUser = saveUser("testuser", "test@example.com");
    }

    // ---------------------------------------------------------------------------------------------------
    // POST 엔드포인트들이 201 CREATED 반환 확인
    // ---------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("POST /auth/register - 201 CREATED 반환")
    void shouldReturn201OnUserRegistration() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");
        request.setPasswordConfirm("password123");
        request.setEmail("newuser@example.com");
        request.setPhone("010-1234-5678");
        request.setImage("profile.png");

        mockMvc.perform(withApiServletPath(post(apiPath("/auth/register")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /organizations - 201 CREATED 반환")
    void shouldReturn201OnOrganizationCreation() throws Exception {
        OrgCreateRequest request = new OrgCreateRequest();
        request.setName("New Organization");
        request.setDescription("Description");
        request.setCategory("CLUB");
        request.setImage_url("logo.png");

        mockMvc.perform(withApiServletPath(post(apiPath("/organizations")))
                        .with(auth(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    // ---------------------------------------------------------------------------------------------------
    // DELETE 엔드포인트들이 204 NO_CONTENT 반환 확인
    // ---------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("DELETE /users/me - 204 NO_CONTENT 반환")
    void shouldReturn204OnUserDeletion() throws Exception {
        UserDeleteRequest request = new UserDeleteRequest();
        request.setPassword("password123");

        mockMvc.perform(withApiServletPath(delete(apiPath("/users/me")))
                        .with(auth(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /organizations/{orgId} - 204 NO_CONTENT 반환")
    void shouldReturn204OnOrganizationDeletion() throws Exception {
        Organization org = saveOrganization("Test Org", testUser);

        mockMvc.perform(withApiServletPath(delete(apiPath("/organizations/{orgId}"), org.getId()))
                        .with(auth(testUser)))
                .andExpect(status().isNoContent());
    }

    // ---------------------------------------------------------------------------------------------------
    // GET 엔드포인트들이 200 OK 반환 확인
    // ---------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("GET /users/me - 200 OK 반환")
    void shouldReturn200OnGetUserInfo() throws Exception {
        mockMvc.perform(withApiServletPath(get(apiPath("/users/me")))
                        .with(auth(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /organizations - 200 OK 반환")
    void shouldReturn200OnGetOrganizationList() throws Exception {
        mockMvc.perform(withApiServletPath(get(apiPath("/organizations")))
                        .with(auth(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /organizations/{orgId} - 200 OK 반환")
    void shouldReturn200OnGetOrganizationDetail() throws Exception {
        Organization org = saveOrganization("Test Org", testUser);

        mockMvc.perform(withApiServletPath(get(apiPath("/organizations/{orgId}"), org.getId()))
                        .with(auth(testUser)))
                .andExpect(status().isOk());
    }

    // ---------------------------------------------------------------------------------------------------
    // PUT/PATCH 엔드포인트들이 200 OK 반환 확인
    // ---------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("PUT /organizations/{orgId} - 200 OK 반환")
    void shouldReturn200OnOrganizationUpdate() throws Exception {
        Organization org = saveOrganization("Test Org", testUser);

        com.example.grouple.dto.organization.request.OrgUpdateRequest request = 
            new com.example.grouple.dto.organization.request.OrgUpdateRequest();
        request.setName("Updated Org");
        request.setDescription("Updated Description");
        request.setCategory("SOCIETY");
        request.setImage_url("updated.png");

        mockMvc.perform(withApiServletPath(put(apiPath("/organizations/{orgId}"), org.getId()))
                        .with(auth(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // ---------------------------------------------------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------------------------------------------------

    private User saveUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password123");
        user.setEmail(email);
        user.setPhone("010-0000-0000");
        user.setImage("image.png");
        return userRepository.saveAndFlush(user);
    }

    private Organization saveOrganization(String name, User owner) {
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
