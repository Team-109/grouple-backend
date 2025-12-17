package com.example.grouple.integration;

import com.example.grouple.dto.schedule.request.ScheduleCreateRequest;
import com.example.grouple.dto.schedule.request.ScheduleModifyRequest;
import com.example.grouple.entity.Organization;
import com.example.grouple.entity.Schedule;
import com.example.grouple.entity.User;
import com.example.grouple.repository.OrganizationRepository;
import com.example.grouple.repository.ScheduleRepository;
import com.example.grouple.repository.UserRepository;
import com.example.grouple.security.AuthPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrgScheduleControllerIntegrationTests extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;

    private User user;
    private Organization org;

    @BeforeEach
    void setup() {
        scheduleRepository.deleteAll();
        organizationRepository.deleteAll();
        userRepository.deleteAll();
        user = saveUser("scheduler");
        org = saveOrganization(user, "Org");
    }

    @Test
    void create_and_get_schedule() throws Exception {
        ScheduleCreateRequest request = new ScheduleCreateRequest();
        request.setTitle("정기 모임");
        request.setDescription("스터디");
        request.setStartTime(LocalDateTime.now().plusDays(1));
        request.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));

        mockMvc.perform(withApiServletPath(post(apiPath("/organizations/{orgId}/schedules"), org.getId()))
                        .with(auth(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("정기 모임"));

        Schedule saved = scheduleRepository.findAll().getFirst();

        mockMvc.perform(withApiServletPath(get(apiPath("/organizations/{orgId}/schedules/{scheduleId}"), org.getId(), saved.getId()))
                        .with(auth(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(saved.getId()))
                .andExpect(jsonPath("$.data.title").value("정기 모임"));
    }

    @Test
    void list_modify_delete_schedule() throws Exception {
        Schedule schedule = scheduleRepository.save(buildSchedule("title1"));

        // 목록
        mockMvc.perform(withApiServletPath(get(apiPath("/organizations/{orgId}/schedules"), org.getId()))
                        .with(auth(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items.length()").value(1));

        // 수정
        ScheduleModifyRequest modify = new ScheduleModifyRequest();
        modify.setTitle("updated");
        modify.setDescription("desc");
        modify.setStartTime(LocalDateTime.now().plusDays(2));
        modify.setEndTime(LocalDateTime.now().plusDays(2).plusHours(1));

        mockMvc.perform(withApiServletPath(patch(apiPath("/organizations/{orgId}/schedules/{scheduleId}"), org.getId(), schedule.getId()))
                        .with(auth(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modify)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("updated"));

        // 삭제
        mockMvc.perform(withApiServletPath(delete(apiPath("/organizations/{orgId}/schedules/{scheduleId}"), org.getId(), schedule.getId()))
                        .with(auth(user)))
                .andExpect(status().isNoContent());

        assertThat(scheduleRepository.existsById(schedule.getId())).isFalse();
    }

    private Schedule buildSchedule(String title) {
        Schedule s = new Schedule();
        s.setTitle(title);
        s.setDescription("desc");
        s.setStartTime(LocalDateTime.now().plusDays(1));
        s.setEndTime(LocalDateTime.now().plusDays(1).plusHours(1));
        s.setUser(user);
        s.setOrganization(org);
        return s;
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
