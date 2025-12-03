package com.example.grouple.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduleTests {

    @Test
    void shouldInstantiateScheduleEntity() {
        Schedule schedule = new Schedule();
        assertThat(schedule).isNotNull();
    }

    @Test
    void shouldHoldBasicFields() {
        Schedule schedule = new Schedule();

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        schedule.setTitle("정기 모임");
        schedule.setDescription("스터디 진행");
        schedule.setStartTime(start);
        schedule.setEndTime(end);

        assertThat(schedule.getTitle()).isEqualTo("정기 모임");
        assertThat(schedule.getDescription()).isEqualTo("스터디 진행");
        assertThat(schedule.getStartTime()).isEqualTo(start);
        assertThat(schedule.getEndTime()).isEqualTo(end);
    }

    @Test
    void shouldHoldUserRelationship() {
        Schedule schedule = new Schedule();
        User user = new User();
        user.setId(10);
        user.setUsername("tester");

        schedule.setUser(user);

        assertThat(schedule.getUser()).isEqualTo(user);
        assertThat(schedule.getUser().getId()).isEqualTo(10);
        assertThat(schedule.getUser().getUsername()).isEqualTo("tester");
    }

    @Test
    void shouldHoldOrganizationRelationship() {
        Schedule schedule = new Schedule();
        Organization org = new Organization();
        org.setId(1);
        org.setName("컴공과");

        schedule.setOrganization(org);

        assertThat(schedule.getOrganization()).isEqualTo(org);
        assertThat(schedule.getOrganization().getId()).isEqualTo(1);
        assertThat(schedule.getOrganization().getName()).isEqualTo("컴공과");
    }
}
