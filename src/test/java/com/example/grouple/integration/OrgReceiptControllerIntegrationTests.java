package com.example.grouple.integration;

import com.example.grouple.dto.receipt.request.ReceiptCreateRequest;
import com.example.grouple.dto.receipt.request.ReceiptUpdateRequest;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.Receipt;
import com.example.grouple.entity.User;
import com.example.grouple.repository.OrganizationRepository;
import com.example.grouple.repository.ReceiptRepository;
import com.example.grouple.repository.UserRepository;
import com.example.grouple.security.AuthPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrgReceiptControllerIntegrationTests extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private ReceiptRepository receiptRepository;

    private User owner;
    private Organization org;

    @BeforeEach
    void setup() {
        receiptRepository.deleteAll();
        organizationRepository.deleteAll();
        userRepository.deleteAll();
        owner = saveUser("owner");
        org = saveOrganization(owner, "Org");
    }

    @Test
    void create_and_get_receipt() throws Exception {
        ReceiptCreateRequest request = new ReceiptCreateRequest(
                "CARD", 1000, "FOOD", LocalDate.of(2025, 1, 1), null, "lunch"
        );

        mockMvc.perform(withApiServletPath(post(apiPath("/organizations/{orgId}/receipts"), org.getId()))
                        .with(auth(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.receiptId").exists());

        Receipt saved = receiptRepository.findAll().getFirst();

        mockMvc.perform(withApiServletPath(get(apiPath("/organizations/{orgId}/receipts/{receiptId}"), org.getId(), saved.getId()))
                        .with(auth(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(saved.getId()))
                .andExpect(jsonPath("$.data.category").value("FOOD"));
    }

    @Test
    void list_receipts() throws Exception {
        for (int i = 0; i < 2; i++) {
            receiptRepository.save(Receipt.builder()
                    .type("CARD")
                    .amount(100 + i)
                    .category("FOOD")
                    .description("d" + i)
                    .date(LocalDate.now())
                    .organization(org)
                    .user(owner)
                    .build());
        }

        mockMvc.perform(withApiServletPath(get(apiPath("/organizations/{orgId}/receipts"), org.getId()))
                        .with(auth(owner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.data.length()").value(2));
    }

    @Test
    void update_and_delete_receipt() throws Exception {
        Receipt receipt = receiptRepository.save(Receipt.builder()
                .type("CARD")
                .amount(1000)
                .category("FOOD")
                .description("desc")
                .date(LocalDate.now())
                .organization(org)
                .user(owner)
                .build());

        ReceiptUpdateRequest update = new ReceiptUpdateRequest(
                "CASH", 2000, "ETC", "updated", LocalDate.now(), null
        );

        mockMvc.perform(withApiServletPath(put(apiPath("/organizations/{orgId}/receipts/{receiptId}"), org.getId(), receipt.getId()))
                        .with(auth(owner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").exists());

        mockMvc.perform(withApiServletPath(delete(apiPath("/organizations/{orgId}/receipts/{receiptId}"), org.getId(), receipt.getId()))
                        .with(auth(owner)))
                .andExpect(status().isNoContent());

        assertThat(receiptRepository.existsById(receipt.getId())).isFalse();
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
