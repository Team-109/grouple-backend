package com.example.grouple.entity.id;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MemberIdTests {

    @Test
    void shouldCompareByCompositeKeys() {
        MemberId first = new MemberId(1, 2);
        MemberId second = new MemberId(1, 2);
        MemberId third = new MemberId(2, 3);

        assertThat(first).isEqualTo(second);
        assertThat(first).hasSameHashCodeAs(second);
        assertThat(first).isNotEqualTo(third);
    }
}
