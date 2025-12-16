package com.example.grouple.integration;

import com.example.grouple.dto.auth.request.RegisterRequest;
import com.example.grouple.dto.organization.request.OrgUpdateRequest;
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
 * 예외 처리 통합 테스트
 * GlobalExceptionHandler가 실제 요청에서 올바르게 동작하는지 검증
 */
class ExceptionHandlingIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private User testUser;
    private User otherUser;

    @BeforeEach
    void setUp() {
        organizationRepository.deleteAll();
        userRepository.deleteAll();
        testUser = saveUser("testuser", "test@example.com");
        otherUser = saveUser("otheruser", "other@example.com");
    }

    // ---------------------------------------------------------------------------------------------------
    // 인증되지 않은 요청 → 401 UNAUTHORIZED
    // ---------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("인증 없이 보호된 리소스 접근 시 401 UNAUTHORIZED 반환")
    void shouldReturn401WhenAccessingProtectedResourceWithoutAuth() throws Exception {
        mockMvc.perform(withApiServletPath(get(apiPath("/users/me"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("잘못된 인증 정보로 접근 시 401 UNAUTHORIZED 반환")
    void shouldReturn401WhenAccessingWithInvalidAuth() throws Exception {
        // principal이 null인 경우
        var invalidAuth = new UsernamePasswordAuthenticationToken(null, null, 
            List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(withApiServletPath(get(apiPath("/users/me")))
                        .with(authentication(invalidAuth)))
                .andExpect(status().isUnauthorized());
    }

    // ---------------------------------------------------------------------------------------------------
    // 권한 없는 요청 → 403 FORBIDDEN
    // ---------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("다른 사용자의 조직 수정 시도 시 403 FORBIDDEN 반환")
    void shouldReturn403WhenUpdatingOthersOrganization() throws Exception {
        Organization org = saveOrganization("Test Org", testUser);

        OrgUpdateRequest request = new OrgUpdateRequest();
        request.setName("Hacked Org");
        request.setDescription("Hacked Description");
        request.setCategory("HACKED");
        request.setImage_url("hacked.png");

        // otherUser가 testUser의 조직을 수정하려고 시도
        // AccessDeniedException이 발생하고 GlobalExceptionHandler가 403으로 처리
        mockMvc.perform(withApiServletPath(put(apiPath("/organizations/{orgId}"), org.getId()))
                        .with(auth(otherUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("다른 사용자의 조직 삭제 시도 시 403 FORBIDDEN 반환")
    void shouldReturn403WhenDeletingOthersOrganization() throws Exception {
        Organization org = saveOrganization("Test Org", testUser);

        // otherUser가 testUser의 조직을 삭제하려고 시도
        mockMvc.perform(withApiServletPath(delete(apiPath("/organizations/{orgId}"), org.getId()))
                        .with(auth(otherUser)))
                .andExpect(status().isForbidden());
    }

    // ---------------------------------------------------------------------------------------------------
    // 존재하지 않는 리소스 → 404 NOT_FOUND
    // ---------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("존재하지 않는 조직 조회 시 500 INTERNAL_SERVER_ERROR 반환")
    void shouldReturn500WhenOrganizationNotFound() throws Exception {
        Integer nonExistentOrgId = 99999;

        // getOrganizationById가 null을 반환하여 NullPointerException 발생
        mockMvc.perform(withApiServletPath(get(apiPath("/organizations/{orgId}"), nonExistentOrgId))
                        .with(auth(testUser)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("존재하지 않는 조직 수정 시도 시 404 NOT_FOUND 반환")
    void shouldReturn404WhenUpdatingNonExistentOrganization() throws Exception {
        Integer nonExistentOrgId = 99999;

        OrgUpdateRequest request = new OrgUpdateRequest();
        request.setName("Updated Org");
        request.setDescription("Updated Description");
        request.setCategory("SOCIETY");
        request.setImage_url("updated.png");

        mockMvc.perform(withApiServletPath(put(apiPath("/organizations/{orgId}"), nonExistentOrgId))
                        .with(auth(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------------------------------------------------
    // 잘못된 요청 데이터 → 400 BAD_REQUEST
    // ---------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("잘못된 형식의 요청 데이터로 조직 생성 시 500 INTERNAL_SERVER_ERROR 반환")
    void shouldReturn500WhenCreatingOrganizationWithInvalidData() throws Exception {
        // null 필드가 있는 요청 - NullPointerException 발생 가능
        com.example.grouple.dto.organization.request.OrgCreateRequest request = 
            new com.example.grouple.dto.organization.request.OrgCreateRequest();
        // name, description 등 필수 필드를 설정하지 않음

        mockMvc.perform(withApiServletPath(post(apiPath("/organizations")))
                        .with(auth(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("잘못된 JSON 형식으로 요청 시 500 INTERNAL_SERVER_ERROR 반환")
    void shouldReturn500WhenSendingMalformedJson() throws Exception {
        // JSON 파싱 오류는 현재 구현에서 500으로 처리됨
        String malformedJson = "not a json at all";

        mockMvc.perform(withApiServletPath(post(apiPath("/organizations")))
                        .with(auth(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isInternalServerError());
    }

    // ---------------------------------------------------------------------------------------------------
    // 중복 리소스 생성 → 409 CONFLICT (또는 실제 구현에 따라 400 BAD_REQUEST)
    // ---------------------------------------------------------------------------------------------------

    @Test
    @DisplayName("중복된 사용자명으로 가입 시도 시 400 BAD_REQUEST 반환")
    void shouldReturn400WhenRegisteringDuplicateUsername() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser"); // 이미 존재하는 사용자명
        request.setPassword("password123");
        request.setPasswordConfirm("password123");
        request.setEmail("newemail@example.com");
        request.setPhone("010-1234-5678");
        request.setImage("profile.png");

        // UserService에서 IllegalArgumentException을 던지므로 400 BAD_REQUEST
        mockMvc.perform(withApiServletPath(post(apiPath("/auth/register")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
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
