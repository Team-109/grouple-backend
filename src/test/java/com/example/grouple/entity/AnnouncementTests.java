package com.example.grouple.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnnouncementTests {

    @Test
    void shouldInstantiateAnnouncementEntity() {
        assertThat(new Announcement()).isNotNull();
    }
}
