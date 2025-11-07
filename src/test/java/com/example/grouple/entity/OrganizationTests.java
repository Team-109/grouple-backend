package com.example.grouple.entity;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationTests {

    @Test
    void shouldGenerateCodeWhenBlank() throws Exception {
        Organization organization = new Organization();
        invokeEnsureCode(organization);

        assertThat(organization.getCode()).isNotBlank();
        assertThat(organization.getCode()).hasSize(6);
    }

    @Test
    void shouldRetainExistingCode() throws Exception {
        Organization organization = new Organization();
        organization.setCode("ABC123");

        invokeEnsureCode(organization);

        assertThat(organization.getCode()).isEqualTo("ABC123");
    }

    @Test
    void shouldHoldOwnerRelationship() {
        User owner = new User();
        owner.setId(10);
        Organization organization = new Organization();
        organization.setOwner(owner);
        Instant now = Instant.now();
        organization.setCreatedAt(now);
        organization.setUpdatedAt(now);

        assertThat(organization.getOwner()).isEqualTo(owner);
        assertThat(organization.getMembers()).isNotNull();
        assertThat(organization.getCreatedAt()).isEqualTo(now);
        assertThat(organization.getUpdatedAt()).isEqualTo(now);
    }

    private void invokeEnsureCode(Organization organization) throws Exception {
        Method method = Organization.class.getDeclaredMethod("ensureCode");
        method.setAccessible(true);
        method.invoke(organization);
    }
}
