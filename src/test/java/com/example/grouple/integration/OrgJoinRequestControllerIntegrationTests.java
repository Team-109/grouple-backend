package com.example.grouple.integration;

import com.example.grouple.dto.joinrequest.request.JoinRequestCreateRequest;
import com.example.grouple.dto.joinrequest.request.JoinRequestDecisionRequest;
import com.example.grouple.entity.JoinRequest;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.User;
import com.example.grouple.repository.JoinRequestRepository;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrgJoinRequestControllerIntegrationTests extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private JoinRequestRepository joinRequestRepository;

    private User owner;
    private User applicant;
    private Organization org;

    @BeforeEach
    void setup() {
        joinRequestRepository.deleteAll();
        organizationRepository.deleteAll();
        userRepository.deleteAll();
        owner = saveUser("owner");
        applicant = saveUser("applicant");
        org = saveOrganization(owner, "Org");
    }

    @Test
    void create_approve_reject_flow() throws Exception {
        // create join request (code param)
        JoinRequestCreateRequest createReq = new JoinRequestCreateRequest();
        createReq.setMessage("가입요청");

        mockMvc.perform(withApiServletPath(post(apiPath("/organizations/{orgId}/join-requests"), org.getId()))
                        .with(auth(applicant))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        JoinRequest saved = joinRequestRepository.findAll().getFirst();

        // approve
        JoinRequestDecisionRequest approveReq = new JoinRequestDecisionRequest();
        approveReq.setReason("ok");

        mockMvc.perform(withApiServletPath(post(apiPath("/organizations/{orgId}/join-requests/{reqId}/approve"), org.getId(), saved.getId()))
                        .with(auth(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approveReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPROVED"));

        // reject another request
        JoinRequest another = new JoinRequest();
        another.setOrganization(org);
        another.setUser(applicant);
        another.setStatus(com.example.grouple.entity.JoinRequestStatus.PENDING);
        another = joinRequestRepository.save(another);
        JoinRequestDecisionRequest rejectReq = new JoinRequestDecisionRequest();
        rejectReq.setReason("nope");

        mockMvc.perform(withApiServletPath(post(apiPath("/organizations/{orgId}/join-requests/{reqId}/reject"), org.getId(), another.getId()))
                        .with(auth(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejectReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("REJECTED"));
    }

    @Test
    void list_join_requests() throws Exception {
        JoinRequest pending = new JoinRequest();
        pending.setOrganization(org);
        pending.setUser(applicant);
        pending.setStatus(com.example.grouple.entity.JoinRequestStatus.PENDING);
        joinRequestRepository.save(pending);

        mockMvc.perform(withApiServletPath(get(apiPath("/organizations/{orgId}/join-requests"), org.getId()))
                        .with(auth(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.requests.length()").value(1));
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
