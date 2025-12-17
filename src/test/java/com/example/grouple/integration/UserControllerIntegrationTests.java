package com.example.grouple.integration;

import com.example.grouple.dto.user.request.UserDeleteRequest;
import com.example.grouple.dto.user.request.UserImageModifyForm;
import com.example.grouple.dto.user.request.UserModifyRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTests extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;

    private User user;

    @BeforeEach
    void setup() {
        organizationRepository.deleteAll();
        userRepository.deleteAll();
        user = saveUser("meuser");
    }

    @Test
    void getMe_shouldReturnProfile() throws Exception {
        mockMvc.perform(withApiServletPath(get(apiPath("/users/me")))
                        .with(auth(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("meuser"))
                .andExpect(jsonPath("$.data.email").value("meuser@example.com"));
    }

    @Test
    void updateMe_shouldModifyProfile() throws Exception {
        UserModifyRequest request = new UserModifyRequest();
        request.setUsername("updated");
        request.setPhone("010-9999-8888");

        mockMvc.perform(withApiServletPath(put(apiPath("/users/me")))
                        .with(auth(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("updated"))
                .andExpect(jsonPath("$.data.phone").value("010-9999-8888"));
    }

    @Test
    void updatePhoto_shouldChangeImage() throws Exception {
        UserImageModifyForm form = new UserImageModifyForm();
        form.setImage("new.png");

        mockMvc.perform(withApiServletPath(put(apiPath("/users/me/image")))
                        .with(auth(user))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .flashAttr("userImageModifyForm", form)
                        .param("image", "new.png"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.image").value("new.png"));
    }

    @Test
    void deleteMe_shouldDeleteUser() throws Exception {
        UserDeleteRequest request = new UserDeleteRequest();
        request.setPassword("pw");

        mockMvc.perform(withApiServletPath(delete(apiPath("/users/me")))
                        .with(auth(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(user.getId())).isFalse();
    }

    @Test
    void getMyOrganizations_shouldReturnOwnedOrgs() throws Exception {
        Organization org = new Organization();
        org.setOwner(user);
        org.setName("My Org");
        org.setDescription("desc");
        org.setCategory("CLUB");
        org.setImage("img");
        organizationRepository.saveAndFlush(org);

        mockMvc.perform(withApiServletPath(get(apiPath("/users/me/organizations")))
                        .with(auth(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].owner_id").value(user.getId()))
                .andExpect(jsonPath("$.data[0].name").value("My Org"));
    }

    private User saveUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("pw");
        user.setEmail(username + "@example.com");
        user.setPhone("010-0000-0000");
        return userRepository.saveAndFlush(user);
    }

    private RequestPostProcessor auth(User user) {
        var principal = new AuthPrincipal(user.getId(), user.getUsername());
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return authentication(new UsernamePasswordAuthenticationToken(principal, null, authorities));
    }
}
