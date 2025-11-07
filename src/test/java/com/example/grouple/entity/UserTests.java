package com.example.grouple.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class UserTests {

    @Test
    void shouldStoreBasicFields() {
        Instant now = Instant.now();

        User user = new User();
        user.setId(1);
        user.setUsername("tester");
        user.setPassword("secret");
        user.setEmail("tester@example.com");
        user.setPhone("010-1234-5678");
        user.setImage("profile.png");
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        assertThat(user.getId()).isEqualTo(1);
        assertThat(user.getUsername()).isEqualTo("tester");
        assertThat(user.getPassword()).isEqualTo("secret");
        assertThat(user.getEmail()).isEqualTo("tester@example.com");
        assertThat(user.getPhone()).isEqualTo("010-1234-5678");
        assertThat(user.getImage()).isEqualTo("profile.png");
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
        assertThat(user.getOwnedOrganizations()).isNotNull();
    }
}
