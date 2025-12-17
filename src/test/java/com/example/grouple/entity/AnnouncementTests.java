package com.example.grouple.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnnouncementTests {

    @Test
    void shouldInstantiateAnnouncementEntity() {
        assertThat(new Announcement()).isNotNull();
    }

    @Test
    void shouldStoreFieldsAndRelations() {
        Announcement announcement = new Announcement();
        User user = new User();
        user.setId(11);
        user.setUsername("owner");
        Organization org = new Organization();
        org.setId(22);
        org.setName("Org");

        announcement.setId(1);
        announcement.setTitle("공지");
        announcement.setDescription("내용");
        announcement.setStar(true);
        announcement.setUser(user);
        announcement.setOrganization(org);

        assertThat(announcement.getId()).isEqualTo(1);
        assertThat(announcement.getTitle()).isEqualTo("공지");
        assertThat(announcement.getDescription()).isEqualTo("내용");
        assertThat(announcement.getStar()).isTrue();
        assertThat(announcement.getUser()).isSameAs(user);
        assertThat(announcement.getOrganization()).isSameAs(org);
    }
}
