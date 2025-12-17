package com.example.grouple.integration;

import com.example.grouple.dto.document.request.DocumentCreateRequest;
import com.example.grouple.dto.document.request.DocumentUpdateRequest;
import com.example.grouple.entity.Document;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.User;
import com.example.grouple.repository.DocumentRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrgDocumentControllerIntegrationTests extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private DocumentRepository documentRepository;

    private User user;
    private Organization org;

    @BeforeEach
    void setup() {
        documentRepository.deleteAll();
        organizationRepository.deleteAll();
        userRepository.deleteAll();
        user = saveUser();
        org = saveOrganization(user, "Org");
    }

    @Test
    void createAndGetDocument_shouldSucceed() throws Exception {
        DocumentCreateRequest request = new DocumentCreateRequest(
                "title", "desc", "file.pdf", "pdf", 100, org.getId()
        );

        mockMvc.perform(withApiServletPath(post(apiPath("/organizations/{orgId}/documents"), org.getId()))
                        .with(auth(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.name").value("file.pdf"));

        Document saved = documentRepository.findAll().getFirst();

        mockMvc.perform(withApiServletPath(get(apiPath("/organizations/{orgId}/documents/{docId}"), org.getId(), saved.getId()))
                        .with(auth(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.documentId").value(saved.getId()))
                .andExpect(jsonPath("$.data.title").value("title"));
    }

    @Test
    void listDocuments_shouldReturnPagedData() throws Exception {
        for (int i = 0; i < 2; i++) {
            documentRepository.save(Document.builder()
                    .title("t" + i)
                    .description("d" + i)
                    .name("f" + i)
                    .type("pdf")
                    .size(10)
                    .organization(org)
                    .user(user)
                    .build());
        }

        mockMvc.perform(withApiServletPath(get(apiPath("/organizations/{orgId}/documents"), org.getId()))
                        .with(auth(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    void updateAndDeleteDocument_shouldPersistChanges() throws Exception {
        Document doc = documentRepository.save(Document.builder()
                .title("old")
                .description("old")
                .name("old.pdf")
                .type("pdf")
                .size(10)
                .organization(org)
                .user(user)
                .build());

        DocumentUpdateRequest updateRequest = new DocumentUpdateRequest();
        updateRequest.setTitle("new title");
        updateRequest.setSize(999);

        mockMvc.perform(withApiServletPath(put(apiPath("/organizations/{orgId}/documents/{docId}"), org.getId(), doc.getId()))
                        .with(auth(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("new title"))
                .andExpect(jsonPath("$.data.size").value(999));

        mockMvc.perform(withApiServletPath(delete(apiPath("/organizations/{orgId}/documents/{docId}"), org.getId(), doc.getId()))
                        .with(auth(user)))
                .andExpect(status().isNoContent());

        assertThat(documentRepository.existsById(doc.getId())).isFalse();
    }

    private User saveUser() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("pw");
        user.setEmail("user@example.com");
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
