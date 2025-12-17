package com.example.grouple.integration;

import com.example.grouple.dto.auth.request.LoginRequest;
import com.example.grouple.dto.auth.request.RefreshTokenRequest;
import com.example.grouple.dto.auth.request.RegisterRequest;
import com.example.grouple.entity.User;
import com.example.grouple.repository.UserRepository;
import com.example.grouple.security.AuthPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerIntegrationTests extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void registerUser_shouldCreateAndReturn201() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("Password1!");
        request.setPasswordConfirm("Password1!");
        request.setEmail("new@example.com");
        request.setPhone("010-1234-5678");

        mockMvc.perform(post(apiPath("/auth/register"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.username").value("newuser"))
                .andExpect(jsonPath("$.data.email").value("new@example.com"));

        assertThat(userRepository.existsByUsername("newuser")).isTrue();
    }

    @Test
    void login_shouldReturnTokens() throws Exception {
        User saved = saveUser("loginuser", "pw123!", "login@example.com");

        LoginRequest request = new LoginRequest();
        request.setUsername(saved.getUsername());
        request.setPassword("pw123!");

        mockMvc.perform(post(apiPath("/auth/login"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    void refresh_shouldIssueNewTokens() throws Exception {
        User saved = saveUser("refreshuser", "pw123!", "ref@example.com");
        // 먼저 로그인으로 refreshToken 발급
        LoginRequest login = new LoginRequest();
        login.setUsername(saved.getUsername());
        login.setPassword("pw123!");
        String refreshToken = objectMapper.readTree(
                mockMvc.perform(post(apiPath("/auth/login"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(login)))
                        .andReturn()
                        .getResponse()
                        .getContentAsString()
        ).path("data").path("refreshToken").asText();

        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);

        mockMvc.perform(post(apiPath("/auth/refresh"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    void me_shouldReturnCurrentUser() throws Exception {
        User saved = saveUser("meuser", "pw123!", "me@example.com");

        mockMvc.perform(withApiServletPath(get(apiPath("/auth/me")))
                        .with(auth(saved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("로그인된 사용자 아이디: " + saved.getUsername()));
    }

    private User saveUser(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone("010-0000-0000");
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.saveAndFlush(user);
    }

    private RequestPostProcessor auth(User user) {
        var principal = new AuthPrincipal(user.getId(), user.getUsername());
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return authentication(new UsernamePasswordAuthenticationToken(principal, null, authorities));
    }
}
