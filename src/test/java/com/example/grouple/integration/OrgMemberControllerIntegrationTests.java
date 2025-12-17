package com.example.grouple.integration;

import com.example.grouple.dto.organization.request.OrgMembersDeleteRequest;
import com.example.grouple.entity.Member;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.User;
import com.example.grouple.entity.id.MemberId;
import com.example.grouple.repository.MemberRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrgMemberControllerIntegrationTests extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private MemberRepository memberRepository;

    private User owner;
    private User memberUser;
    private Organization org;

    @BeforeEach
    void setup() {
        memberRepository.deleteAll();
        organizationRepository.deleteAll();
        userRepository.deleteAll();
        owner = saveUser("owner");
        memberUser = saveUser("member");
        org = saveOrganization(owner, "Org");
        saveMember(org, owner, "OWNER");
        saveMember(org, memberUser, "MEMBER");
    }

    @Test
    void listMembers_shouldReturnMembers() throws Exception {
        mockMvc.perform(withApiServletPath(get(apiPath("/organizations/{orgId}/members"), org.getId()))
                        .with(auth(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.organizationId").value(org.getId()))
                .andExpect(jsonPath("$.data.members.length()").value(2));
    }

    @Test
    void filterMembersByRole_shouldReturnFiltered() throws Exception {
        mockMvc.perform(withApiServletPath(get(apiPath("/organizations/{orgId}/members"), org.getId()))
                        .with(auth(owner))
                        .param("role", "MEMBER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.members.length()").value(1))
                .andExpect(jsonPath("$.data.members[0].role").value("MEMBER"));
    }

    @Test
    void deleteMember_asOwner_shouldSucceed() throws Exception {
        OrgMembersDeleteRequest request = new OrgMembersDeleteRequest();
        request.setReason("cleanup");

        mockMvc.perform(withApiServletPath(delete(apiPath("/organizations/{orgId}/members/{memberId}"), org.getId(), memberUser.getId()))
                        .with(auth(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        assertThat(memberRepository.existsById(new MemberId(memberUser.getId(), org.getId()))).isFalse();
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

    private Member saveMember(Organization org, User user, String role) {
        Member member = new Member();
        member.setId(new MemberId(user.getId(), org.getId()));
        member.setOrganization(org);
        member.setUser(user);
        member.setRole(role);
        member.setJoinedAt(Instant.now());
        return memberRepository.saveAndFlush(member);
    }

    private RequestPostProcessor auth(User user) {
        var principal = new AuthPrincipal(user.getId(), user.getUsername());
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return authentication(new UsernamePasswordAuthenticationToken(principal, null, authorities));
    }
}
