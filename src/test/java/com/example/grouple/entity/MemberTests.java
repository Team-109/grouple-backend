package com.example.grouple.entity;

import com.example.grouple.entity.id.MemberId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class MemberTests {

    @Test
    void shouldLinkUserAndOrganization() {
        User user = new User();
        user.setId(1);
        Organization organization = new Organization();
        organization.setId(2);
        MemberId id = new MemberId(1, 2);

        Member member = new Member();
        member.setId(id);
        member.setUser(user);
        member.setOrganization(organization);
        member.setRole("ADMIN");
        member.setJoinedAt(Instant.EPOCH);

        assertThat(member.getId()).isEqualTo(id);
        assertThat(member.getUser()).isEqualTo(user);
        assertThat(member.getOrganization()).isEqualTo(organization);
        assertThat(member.getRole()).isEqualTo("ADMIN");
        assertThat(member.getJoinedAt()).isEqualTo(Instant.EPOCH);
    }
}
