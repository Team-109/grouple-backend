package com.example.grouple.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnnouncementControllerTests {

    @Test
    void shouldInstantiateController() {
        assertThat(new OrgAnnouncementController(null)).isNotNull();
    }
}
